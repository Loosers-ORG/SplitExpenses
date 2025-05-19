import { useState, useEffect } from "react";
import styles from "./splitScreen.module.css";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function SplitScreenPage() {
  const [groups, setGroups] = useState([]);
  const [selectedGroup, setSelectedGroup] = useState(null); // State to store the selected group
  const [users, setUsers] = useState([]); // List of all users
  const [groupUsers, setGroupUsers] = useState([]); // List of group users
  const [expenses, setExpenses] = useState([]); // Expenses for the selected group
  const [transactions, setTransactions] = useState([]); // Transactions for the selected group
  const [newGroupName, setNewGroupName] = useState("");
  const [newGroupDescription, setNewGroupDescription] = useState("");
  const [selectedUsers, setSelectedUsers] = useState([]); // Selected users for the group
  const [isPopupOpen, setIsPopupOpen] = useState(false); // State to control popup visibility
  const [isPopupOpenExpenses, setIsPopupOpenExpenses] = useState(false); // State to control popup visibility for expenses
  const [isLoading, setIsLoading] = useState(false); // State to show busy indicator
  const [currGroupName, setCurrGroupName] = useState("");
  const [expenseName, setExpenseName] = useState("");
  const [expenseDescription, setExpenseDescription] = useState("");
  const [expenseAmount, setExpenseAmount] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [editingExpenseId, setEditingExpenseId] = useState(null); // Store the ID of the expense being edited
  const queryParams = new URLSearchParams(location.search);
  const logedInUser = queryParams.get("email");
  

  useEffect(() => {
    // Fetch the list of groups
    const fetchGroups = async () => {
      try {
        const response = await fetch(
          `https://splitexpenses-tqed.onrender.com/group?userId=${logedInUser}`
        );
        if (response.ok) {
          const data = await response.json();
          setGroups(data);
        } else {
          console.error("Failed to fetch groups");
        }
      } catch (error) {
        console.error("Error fetching groups:", error);
      }
    };

    fetchGroups();
    fetchExpenses();
    fetchUsers();
    fetchTransactions();
  }, []);

  const handleGroupClick = async (group) => {
    setSelectedGroup(group.groupId);
    setCurrGroupName(group.name);

    // Fetch users in the group
    try {
      const response = await fetch(
        `https://splitexpenses-tqed.onrender.com/group/users?groupId=${group.groupId}`
      );
      if (response.ok) {
        const data = await response.json();
        setGroupUsers(data);
      } else {
        console.error("Failed to fetch users");
      }
    } catch (error) {
      console.error("Error fetching users:", error);
    }

    // Fetch expenses for the group
    try {
      const response = await fetch(
        `https://splitexpenses-tqed.onrender.com/expense?groupId=${group.groupId}`
      );
      if (response.ok) {
        const data = await response.json();
        setExpenses(data);
      } else {
        console.error("Failed to fetch expenses");
      }
    } catch (error) {
      console.error("Error fetching expenses:", error);
    }

    // Fetch transactions for the group
    try {
      const response = await fetch(
        `https://splitexpenses-tqed.onrender.com/settlement?groupId=${group.groupId}`
      );
      if (response.ok) {
        const data = await response.json();
        setTransactions(data);
      } else {
        console.error("Failed to fetch transactions");
      }
    } catch (error) {
      console.error("Error fetching transactions:", error);
    }
  };

  const fetchExpenses = async () => {
    try {
      const response = await fetch(
        `https://splitexpenses-tqed.onrender.com/expense?groupId=${selectedGroup}`
      );
      if (response.ok) {
        const data = await response.json();
        setExpenses(data);
      } else {
        console.error("Failed to fetch expenses");
      }
    } catch (error) {
      console.error("Error fetching expenses:", error);
    }
  };

  const fetchTransactions = async () => {
    try {
      const response = await fetch(
        `https://splitexpenses-tqed.onrender.com/settlement?groupId=${selectedGroup}`
      );
      if (response.ok) {
        const data = await response.json();
        const filteredTransactions = data.filter(
          (transaction) =>
            transaction.sender === logedInUser || transaction.receiver === logedInUser
        );
        setTransactions(filteredTransactions);
      } else {
        console.error("Failed to fetch transactions");
      }
    } catch (error) {
      console.error("Error fetching transactions:", error);
    }
  };

  const handleDeleteExpense = async (expenseId) => {
    try {
        // expenseId = expenseId.split("_")[2];
      const response = await fetch(
        `https://splitexpenses-tqed.onrender.com/expense?expenseId=${expenseId}`,
        {
          method: "DELETE",
        }
      );

      if (response.ok) {
        // Remove the deleted expense from the state
        setExpenses((prevExpenses) =>
          prevExpenses.filter((expense) => expense.expenseId !== expenseId)
        );
        fetchTransactions();
        toast.success("Expense deleted successfully!");
      } else {
        console.error("Failed to delete expense");
        toast.error("Failed to delete expense. Please try again.");
      }
    } catch (error) {
      console.error("Error deleting expense:", error);
      toast.error("An error occurred. Please try again.");
    }
  };


  const handleAddExpense = async () => {
    if (!expenseName.trim() || expenseAmount === 0 || !expenseAmount === "") {
      toast.error("Name, and amount cannot be empty");
      return;
    }

    if (selectedUsers.length === 0) {
      toast.error("please select at least one user");
      return;
    }

    try {
      const url = isEditing
        ? `https://splitexpenses-tqed.onrender.com/expense` // PUT URL for editing
        : `https://splitexpenses-tqed.onrender.com/expense?groupId=${selectedGroup}`; // POST URL for adding

      const method = isEditing ? "PUT" : "POST"; // Use PATCH for editing, POST for adding

      const response = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          expenseId: isEditing ? editingExpenseId : Date.now(), // Include expenseId only when editing
          name: expenseName,
          description: expenseDescription,
          amount: parseFloat(expenseAmount),
          paidBy: logedInUser, // Logged-in user's email
          usersIncludedInExpense: selectedUsers, // Selected user emails
        }),
      });

      if (response.ok) {
        setIsPopupOpenExpenses(false); // Close the popup
        setExpenseName(""); // Reset the form fields
        setExpenseDescription("");
        setExpenseAmount("");
        setSelectedUsers([]);
        fetchExpenses(); // Refresh expenses
        fetchTransactions(); // Refresh transactions
        toast.success(
          isEditing
            ? "Expense updated successfully!"
            : "Expense added successfully!"
        );
      } else {
        console.error("Failed to save expense");
        toast.error("Failed to save expense. Please try again.");
      }
    } catch (error) {
      console.error("Error saving expense:", error);
    }
  };

  const handleUserSelection = (userEmail, setSelectedUsers) => {
    setSelectedUsers((prev) => {
      const isSelected = prev.includes(userEmail);
      if (isSelected) {
        // If the user is already selected, remove their email
        return prev.filter((email) => email !== userEmail);
      } else {
        // Otherwise, add the user's email to the list
        return [...prev, userEmail];
      }
    });
    console.log("selected users ", selectedUsers)
  };

  const handleCreateGroup = async () => {
    if (!newGroupName.trim()) {
      return;
    }

    if (selectedUsers.length === 0) {
      return;
    }

    setIsLoading(true); // Show busy indicator

    try {
      const newGroupId = Date.now(); // Generate a unique ID for the group
      const response = await fetch(
        "https://splitexpenses-tqed.onrender.com/group",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            groupId: newGroupId,
            name: newGroupName,
            description: newGroupDescription,
            createdBy: logedInUser, // Replace with the logged-in user's email
            userIds: selectedUsers, // Include the logged-in user in the group
          }),
        }
      );

      if (response.ok) {
        const newGroup = {
          groupId: newGroupId,
          name: newGroupName,
          description: newGroupDescription,
        };

        setGroups([...groups, newGroup]);
        setNewGroupName("");
        setNewGroupDescription("");
        setSelectedUsers([]);
        setIsPopupOpen(false); // Close the popup after success
      } else {
        console.error("Failed to create group");
      }
    } catch (error) {
      console.error("Error creating group:", error);
    } finally {
      setIsLoading(false); // Hide busy indicator
    }
  };

  const fetchUsers = async () => {
    try {
      const response = await fetch(
        "https://splitexpenses-tqed.onrender.com/users"
      );
      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      } else {
        console.error("Failed to fetch users");
      }
    } catch (error) {
      console.error("Error fetching users:", error);
    }
  };

  const handleEditExpense = (expense) => {
    setExpenseName(expense.name);
    setExpenseDescription(expense.description);
    setExpenseAmount(expense.amount);
    setSelectedUsers(expense.users.map((user) => user.email) || []);
    setEditingExpenseId(expense.expenseId); // Set the ID of the expense being edited
    setIsEditing(true); // Set editing mode
    setIsPopupOpenExpenses(true); // Open the popup for editing
  };

  return (
    <div className={styles.container}>
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
              onClick={() => handleGroupClick(group)}
            >
              <div><strong>{group.name}</strong></div>
              <div><small>{group.description}</small></div> 
            </li>
          ))}
        </ul>
      </div>

      {/* Right Pane: Group Details */}
      <div
        className={`${styles.rightPane} ${
          selectedGroup ? styles.rightPaneOpen : ""
        }`}
      >
        {selectedGroup ? (
          <div>
            <div className={styles.header}>
              <h2>Group Details - {currGroupName}</h2>
              <button
                onClick={() => {
                  setIsPopupOpenExpenses(true)
                  selectedUsers.push(logedInUser); // Include the logged-in user in the group
                }}
                className={styles.createGroupButton}
              >
                Add Expense
              </button>
            </div>
            <h3>Transactions</h3>
            <ul className={styles.transactionList}>
              {transactions.map((transaction) => (
                <li
                  key={transaction.settlementId}
                >
                  {transaction.sender === logedInUser ? (
                    <p>
                      You owe <strong>{transaction.receiver}</strong> 
                      <small className={styles.sender}> ₹{transaction.amount.toFixed(2)}</small>
                    </p>
                  ) : (
                    <p>
                      <strong>{transaction.sender}</strong> owes you 
                      <small className={styles.receiver}> ₹{transaction.amount.toFixed(2)}</small>
                    </p>
                  )}
                </li>
              ))}
            </ul>

            <h3>Expenses</h3>
            <ul className={styles.groupList}>
              {expenses.map((expense) => (
                <li key={expense.expenseId} className={styles.groupItem}>
                 <div className={styles.expenseDetails}>
                  <div>
                    <strong>{expense.name}</strong>
                    <p>Paid by: {expense.paidBy}</p>
                    <p>Amount: ₹{expense.amount.toFixed(2)}</p>
                  </div>
                  <div className={styles.actionButtons}>
                    <button
                      onClick={() => handleEditExpense(expense)} // Open the edit popup
                      className={styles.editButton}
                    >
                      ✏️
                    </button>
                    <button
                      onClick={() => handleDeleteExpense(expense.expenseId)} // Delete the expense
                      className={styles.deleteButton}
                    >
                      🗑️
                    </button>
                  </div>
                </div>
                </li>
              ))}
            </ul>
          </div>
        ) : (
          <div className={styles.noExpenses}>
            <p>Select a group to view details</p>
          </div>
        )}
      </div>
      <ToastContainer
        position="top-center" // Position the toast at the top-center
        autoClose={1200} // Automatically close after 3 seconds
        hideProgressBar={true} // Hide the progress bar
        newestOnTop={true} // Show the newest toast on top
        closeOnClick // Close the toast when clicked
        rtl={false} // Disable right-to-left layout
        pauseOnFocusLoss // Pause the timer when the window loses focus
        draggable // Allow dragging the toast
        pauseOnHover // Pause the timer when hovering over the toast
        theme="dark"
      />

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
              {users
                .filter((user) => user.email !== logedInUser) // Exclude the logged-in user
                .map((user) => (
                  <li key={user.email} className={styles.userItem}>
                    <label>
                      <input
                        type="checkbox"
                        checked={selectedUsers.includes(user.email)} // Bind to selectedUsers state
                        onChange={() =>
                          handleUserSelection(user.email, setSelectedUsers)
                        } // Pass user email to handleUserSelection
                        style={{ marginRight: "10px" }}
                      />
                      {user.name} ({user.email})
                    </label>
                  </li>
                ))}
            </ul>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <button
                onClick={handleCreateGroup}
                className={styles.saveButton}
                disabled={isLoading} // Disable button while loading
              >
                {isLoading ? "Saving..." : "Save"}
              </button>
              <button
                onClick={() => {
                  setIsPopupOpen(false)
                  setSelectedUsers([])
                }} // Close the popup
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
            <h2>Add Expense</h2>
            <input
              type="text"
              placeholder="Name"
              value={expenseName}
              onChange={(e) => setExpenseName(e.target.value)}
              className={styles.inputField}
            />
            <input
              type="text"
              placeholder="Description"
              value={expenseDescription}
              onChange={(e) => setExpenseDescription(e.target.value)}
              className={styles.inputField}
            />
            <input
              type="number"
              placeholder="Amount"
              value={expenseAmount}
              onChange={(e) => setExpenseAmount(e.target.value)}
              className={styles.inputField}
            />
            <h3>Select Users</h3>
            <ul className={styles.userList}>
              {groupUsers.map((user) => (
                <li key={user.email} className={styles.userItem}>
                  <label>
                    <input
                      type="checkbox"
                      disabled={user.email === logedInUser}
                      checked={selectedUsers.includes(user.email)} // Bind to selectedUsers state
                      onChange={() =>
                        handleUserSelection(user.email, setSelectedUsers)
                      } // Pass user email to handleUserSelection
                      style={{ marginRight: "10px" }}
                    />
                    {user.name} ({user.email})
                  </label>
                </li>
              ))}
            </ul>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <button
                onClick={handleAddExpense}
                className={styles.saveButton}
                disabled={isLoading} // Disable button while loading
              >
                {isLoading ? "Saving..." : "Save"}
              </button>
              <button
                onClick={() => {
                  console.log("before calcel: ",selectedUsers);
                  setIsPopupOpenExpenses(false)
                  setSelectedUsers([])
                }} // Close the popup
                className={styles.cancelButton}
                disabled={isLoading} // Disable cancel button while loading
              >
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
