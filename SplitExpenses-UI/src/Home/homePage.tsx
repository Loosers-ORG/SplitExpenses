import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import styles from './home.module.css';

function HomePage() {
  const [groups, setGroups] = useState([]);
  const [newGroupName, setNewGroupName] = useState('');
  const [newGroupDescription, setNewGroupDescription] = useState('');
  const [users, setUsers] = useState([]); // List of all users
  const [selectedUsers, setSelectedUsers] = useState([]); // Selected users for the group
  const [isPopupOpen, setIsPopupOpen] = useState(false); // State to control popup visibility
  const navigate = useNavigate();

  const queryParams = new URLSearchParams(location.search);
  const logedInUser =  queryParams.get('email');

  useEffect(() => {
    // Fetch the list of groups for the user
    const fetchGroups = async () => {
      try {
        const response = await fetch(`https://splitexpenses-tqed.onrender.com/group?userId=${logedInUser}`);
        if (response.ok) {
          const data = await response.json();
          setGroups(data);
        } else {
          console.error('Failed to fetch groups');
        }
      } catch (error) {
        console.error('Error fetching groups:', error);
      }
    };

    fetchGroups();
  }, []);

const handleCreateGroup = async () => {
  if (!newGroupName.trim() || !newGroupDescription.trim()) {
    alert('Group name and description cannot be empty');
    return;
  }

  if (selectedUsers.length === 0) {
    alert('Please select at least one user');
    return;
  }
  try {
    const newGroupId =  Date.now(); // Generate a unique ID for the group
    const response = await fetch('https://splitexpenses-tqed.onrender.com/group', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        groupId: newGroupId,// Generate a unique ID for the group
        name: newGroupName,
        description: newGroupDescription,
        createdBy: encodeURIComponent(logedInUser) , // Replace with the logged-in user's email
        userIds: selectedUsers, 
      }),
    });

    if (response.ok) {
      const newGroup = {
        groupId: newGroupId,
        name: newGroupName,
        description: newGroupDescription,
      }

      setGroups([...groups, newGroup]);
      setNewGroupName('');
      setNewGroupDescription('');
      setSelectedUsers([]);
      setIsPopupOpen(false);
      alert('Group created successfully!');
    } else {
      console.error('Failed to create group');
      alert('Failed to create group. Please try again.');
    }
  } catch (error) {
    console.error('Error creating group:', error);
    alert('An error occurred. Please try again.');
  }
};
  const fetchUsers = async () => {
    try {
      const response = await fetch('https://splitexpenses-tqed.onrender.com/users');
      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      } else {
        console.error('Failed to fetch users');
      }
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

const handleUserSelection = (userEmail) => {
  setSelectedUsers((prev) => {
    if (prev.includes(userEmail)) {
      // If the user is already selected, remove them
      return prev.filter((email) => email !== userEmail);
    } else {
      // Otherwise, add the user to the selected list
      return [...prev, userEmail];
    }
  });
};

  return (
    <div className={styles.container}>
      <h1>Welcome to Split Expenses App</h1>

      <div className={styles.groupList}>
        <h2>Your Groups</h2>
        <ul>
          {groups.map((group) => (
            <li
              key={group.groupId}
              className={styles.groupItem}
              onClick={() => navigate(`/group/${group.groupId}?email=${logedInUser}`)} // Navigate to group details page
            >
              <strong>{group.name}</strong>
              <p>{group.description}</p>
              <small>Created by: {group.createdBy}</small>
            </li>
          ))}
        </ul>
        <div style={{ marginTop: '20px' }}>
          <button
            onClick={() => {
              setIsPopupOpen(true);
              fetchUsers(); // Fetch users when the popup opens
            }}
            className={styles.createGroupButton}
          >
            Create Group
          </button>
        </div>
      </div>

      {/* Popup for Creating Group */}
      {isPopupOpen && (
        <div className={styles.popupOverlay}>
          <div className={styles.popupContent}>
            <h2>Create Group</h2>
            <input
              type="text"
              placeholder="Group Name"
              value={newGroupName}
              onChange={(e) => setNewGroupName(e.target.value)}
              className={styles.inputField}
            />
            <input
              type="text"
              placeholder="Group Description"
              value={newGroupDescription}
              onChange={(e) => setNewGroupDescription(e.target.value)}
              className={styles.inputField}
            />
            <h3>Select Users</h3>
                        <ul className={styles.userList}>
              {users.map((user) => (
                <li key={user.email} className={styles.userItem}>
                  <label>
                    <input
                      type="checkbox"
                      checked={selectedUsers.includes(user.email)} // Bind to selectedUsers state
                      onChange={() => handleUserSelection(user.email)} // Call handleUserSelection on change
                      style={{ marginRight: '10px' }}
                    />
                    {user.name} ({user.email})
                  </label>
                </li>
              ))}
            </ul>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <button onClick={handleCreateGroup} className={styles.saveButton}>
                Save
              </button>
              <button onClick={() => setIsPopupOpen(false)} className={styles.cancelButton}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default HomePage;