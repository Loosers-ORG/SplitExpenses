import { useState, useEffect } from 'react';
import styles from './splitScreen.module.css';

function SplitScreenPage() {
  const [groups, setGroups] = useState([]);
  const [selectedGroup, setSelectedGroup] = useState(null); // State to store the selected group
  const [users, setUsers] = useState([]); // List of all users
  const [expenses, setExpenses] = useState([]); // Expenses for the selected group
  const [transactions, setTransactions] = useState([]); // Transactions for the selected group
  const [newGroupName, setNewGroupName] = useState('');
  const [newGroupDescription, setNewGroupDescription] = useState('');
  const [selectedUsers, setSelectedUsers] = useState([]); // Selected users for the group
  const [isPopupOpen, setIsPopupOpen] = useState(false); // State to control popup visibility
  const [isPopupOpenExpenses, setIsPopupOpenExpenses] = useState(false); // State to control popup visibility for expenses
  const [isLoading, setIsLoading] = useState(false); // State to show busy indicator
  const [newExpense, setNewExpense] = useState({
    name: '',
    description: '',
    amount: '',
    selectedUsers: [],
  });

  const queryParams = new URLSearchParams(location.search);
  const logedInUser =  queryParams.get('email');

  useEffect(() => {
    // Fetch the list of groups
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
    fetchExpenses();
    fetchUsers();
    fetchTransactions();
  }, []);

  const handleGroupClick = async (groupId) => {
    setSelectedGroup(groupId);

    // Fetch users in the group
    try {
      const response = await fetch(`https://splitexpenses-tqed.onrender.com/group/users?groupId=${groupId}`);
      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      } else {
        console.error('Failed to fetch users');
      }
    } catch (error) {
      console.error('Error fetching users:', error);
    }

    // Fetch expenses for the group
    try {
      const response = await fetch(`https://splitexpenses-tqed.onrender.com/expense?groupId=${groupId}`);
      if (response.ok) {
        const data = await response.json();
        setExpenses(data);
      } else {
        console.error('Failed to fetch expenses');
      }
    } catch (error) {
      console.error('Error fetching expenses:', error);
    }

    // Fetch transactions for the group
    try {
      const response = await fetch(`https://splitexpenses-tqed.onrender.com/settlement?groupId=${groupId}`);
      if (response.ok) {
        const data = await response.json();
        setTransactions(data);
      } else {
        console.error('Failed to fetch transactions');
      }
    } catch (error) {
      console.error('Error fetching transactions:', error);
    }
  };


  const fetchExpenses = async () => {
    try {
      const response = await fetch(`https://splitexpenses-tqed.onrender.com/expense?groupId=${selectedGroup}`);
      if (response.ok) {
        const data = await response.json();
        setExpenses(data);
      } else {
        console.error('Failed to fetch expenses');
      }
    } catch (error) {
      console.error('Error fetching expenses:', error);
    }
  };

  const fetchTransactions = async () => {
    try {
      const response = await fetch(`https://splitexpenses-tqed.onrender.com/settlement?groupId=${selectedGroup}`);
      if (response.ok) {
        const data = await response.json();
        const filteredTransactions = data.filter(
          (transaction) => transaction.sender === email || transaction.receiver === email
        );
        setTransactions(filteredTransactions);
      } else {
        console.error('Failed to fetch transactions');
      }
    } catch (error) {
      console.error('Error fetching transactions:', error);
    }
  };

  const handleAddExpense = async () => {
    if (!newExpense.name.trim() || !newExpense.description.trim() || !newExpense.amount.trim()) {
      return;
    }

    if (newExpense.selectedUsers.length === 0) {
      return;
    }

    try {
      const response = await fetch(`https://splitexpenses-tqed.onrender.com/expense?groupId=${selectedGroup}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          expenseId: `${selectedGroup}_${Date.now()}`, // Generate a unique expenseId
          name: newExpense.name,
          description: newExpense.description,
          amount: parseFloat(newExpense.amount),
          paidBy: logedInUser,
          usersIncludedInExpense: newExpense.selectedUsers.map((user) => user.email),
        }),
      });

      if (response.ok) {
        
        setIsPopupOpenExpenses(false);
        fetchExpenses();
        fetchTransactions();

      } else {
        console.error('Failed to add expense');
      }
    } catch (error) {
      console.error('Error adding expense:', error);
    }
  };

  const handleUserSelection = (user) => {
    setNewExpense((prev) => {
      const isSelected = prev.selectedUsers.some((u) => u.email === user.email);
      if (isSelected) {
        return {
          ...prev,
          selectedUsers: prev.selectedUsers.filter((u) => u.email !== user.email),
        };
      } else {
        return {
          ...prev,
          selectedUsers: [...prev.selectedUsers, user],
        };
      }
    });
  };

  const handleCreateGroup = async () => {
    if (!newGroupName.trim() || !newGroupDescription.trim()) {
      return;
    }

    if (selectedUsers.length === 0) {
      return;
    }

    setIsLoading(true); // Show busy indicator

    try {
      const newGroupId = Date.now(); // Generate a unique ID for the group
      const response = await fetch('https://splitexpenses-tqed.onrender.com/group', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          groupId: newGroupId,
          name: newGroupName,
          description: newGroupDescription,
          createdBy: logedInUser, // Replace with the logged-in user's email
          userIds: selectedUsers,
        }),
      });

      if (response.ok) {
        const newGroup = {
          groupId: newGroupId,
          name: newGroupName,
          description: newGroupDescription,
        };

        setGroups([...groups, newGroup]);
        setNewGroupName('');
        setNewGroupDescription('');
        setSelectedUsers([]);
        setIsPopupOpen(false); // Close the popup after success
       
      } else {
        console.error('Failed to create group');
      
      }
    } catch (error) {
      console.error('Error creating group:', error);
     
    } finally {
      setIsLoading(false); // Hide busy indicator
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


 return (
  <div className={styles.splitScreen}>
    {/* Left Pane: Group List */}
    <div className={styles.leftPane}>
      <div className={styles.header}>
        <h2>Your Groups</h2>
        <button
          onClick={() => {
            setIsPopupOpen(true); // Open the popup
            fetchUsers(); // Fetch users when the popup opens
          }}
          className={styles.createGroupButton}
        >
          Create Group
        </button>
      </div>
      <ul className={styles.groupList}>
        {groups.map((group) => (
          <li
            key={group.groupId}
            className={styles.groupItem}
            onClick={() => handleGroupClick(group.groupId)}
          >
            <strong>{group.name}</strong>
            <p>{group.description}</p>
            <small>Created by: {group.createdBy}</small>
          </li>
        ))}
      </ul>
    </div>

    {/* Right Pane: Group Details */}
    <div
      className={`${styles.rightPane} ${
        selectedGroup ? styles.rightPaneOpen : ''
      }`}
    >
      {selectedGroup ? (
        <div>
          <div className={styles.header}>
        <h2>Group Details - {}</h2>
        <button
        onClick={() => setIsPopupOpenExpenses(true)}
          className={styles.createGroupButton}
        >
          Add Expense
        </button>
      </div>
          <h3>Transactions</h3>
          <ul className={styles.transactionList}>
            {transactions.map((transaction) => (
              <li key={transaction.settlementId} className={styles.transactionItem}>
                {transaction.sender === 'currentUser@example.com' ? (
                  <p>
                    You owe <strong>{transaction.receiver}</strong> ₹{transaction.amount.toFixed(2)}
                  </p>
                ) : (
                  <p>
                    <strong>{transaction.sender}</strong> owes you ₹{transaction.amount.toFixed(2)}
                  </p>
                )}
              </li>
            ))}
          </ul>

          <h3>Expenses</h3>
          <ul className={styles.expenseList}>
            {expenses.map((expense) => (
              <li key={expense.expenseId} className={styles.expenseItem}>
                <p>Paid by: {expense.paidBy}</p>
                <p>Amount: ₹{expense.amount.toFixed(2)}</p>
              </li>
            ))}
          </ul>
        </div>
      ) : (
        <p>Select a group to view details</p>
      )}
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
                    checked={selectedUsers.includes(user.email)}
                    onChange={() => handleUserSelection(user.email)}
                    style={{ marginRight: '10px' }}
                  />
                  {user.name} ({user.email})
                </label>
              </li>
            ))}
          </ul>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <button
              onClick={handleCreateGroup}
              className={styles.saveButton}
              disabled={isLoading} // Disable button while loading
            >
              {isLoading ? 'Saving...' : 'Save'}
            </button>
            <button
              onClick={() => setIsPopupOpen(false)} // Close the popup
              className={styles.cancelButton}
              disabled={isLoading} // Disable cancel button while loading
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    )}

    {/* Popup for Adding Expense */}
    {isPopupOpenExpenses && (
  <div className={styles.popupOverlay}>
    <div className={styles.popupContent}>
      <h2 className={styles.popupTitle}>Add Expense</h2>

      <input
        type="text"
        placeholder="Name"
        value={newExpense.name}
        onChange={(e) => setNewExpense({ ...newExpense, name: e.target.value })}
        className={styles.inputField}
      />

      <input
        type="text"
        placeholder="Description"
        value={newExpense.description}
        onChange={(e) => setNewExpense({ ...newExpense, description: e.target.value })}
        className={styles.inputField}
      />

      <input
        type="number"
        placeholder="Amount"
        value={newExpense.amount}
        onChange={(e) => setNewExpense({ ...newExpense, amount: e.target.value })}
        className={styles.inputField}
      />

      <h3 className={styles.userSelectionTitle}>Select Users</h3>
      <ul className={styles.userList}>
        {users.map((user) => (
          <li key={user.email} className={styles.userListItem}>
            <label className={styles.userLabel}>
              <input
                type="checkbox"
                checked={newExpense.selectedUsers.some((u) => u.email === user.email)}
                onChange={() => handleUserSelection(user)}
                className={styles.userCheckbox}
              />
              {user.name} ({user.email})
            </label>
          </li>
        ))}
      </ul>

      <div className={styles.buttonGroup}>
        <button onClick={handleAddExpense} className={styles.saveButton}>
          Save
        </button>
        <button onClick={() => setIsPopupOpenExpenses(false)} className={styles.cancelButton}>
          Cancel
        </button>
      </div>
    </div>
  </div>
)}

  </div>
);
}

export default SplitScreenPage;