import { useState, useEffect } from 'react';
import { useParams, useLocation } from 'react-router-dom';

function GroupDetailsPage() {
  const { groupId } = useParams();
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const email = queryParams.get('email');

  const [expenses, setExpenses] = useState([]);
  const [transactions, setTransactions] = useState([]); // State for transactions
  const [users, setUsers] = useState([]); // State to store users in the group
  const [newExpense, setNewExpense] = useState({
    name: '',
    description: '',
    amount: '',
    selectedUsers: [],
  });
  const [isPopupOpen, setIsPopupOpen] = useState(false);


  useEffect(() => {
    // Fetch expenses for the group
   

    // Fetch users in the group
    const fetchUsers = async () => {
      try {
        const response = await fetch(`https://splitexpenses-tqed.onrender.com/group/users?groupId=${groupId}`);
        if (response.ok) {
          const data = await response.json();
          setUsers(data); // Assuming `data` is an array of users
        } else {
          console.error('Failed to fetch users');
        }
      } catch (error) {
        console.error('Error fetching users:', error);
      }
    };

    // Fetch transactions for the group
    

    fetchExpenses();
    fetchUsers();
    fetchTransactions();
  }, [groupId, email]);

  const fetchExpenses = async () => {
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
  };

  const fetchTransactions = async () => {
    try {
      const response = await fetch(`https://splitexpenses-tqed.onrender.com/settlement?groupId=${groupId}`);
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
      alert('Name, description, and amount cannot be empty');
      return;
    }

    if (newExpense.selectedUsers.length === 0) {
      alert('Please select at least one user');
      return;
    }

    try {
      const response = await fetch(`https://splitexpenses-tqed.onrender.com/expense?groupId=${groupId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          expenseId: `${groupId}_${Date.now()}`, // Generate a unique expenseId
          name: newExpense.name,
          description: newExpense.description,
          amount: parseFloat(newExpense.amount),
          paidBy: email,
          usersIncludedInExpense: newExpense.selectedUsers.map((user) => user.email),
        }),
      });

      if (response.ok) {
        
        setIsPopupOpen(false);
        fetchExpenses();
        fetchTransactions();

        alert('Expense added successfully!');
      } else {
        console.error('Failed to add expense');
        alert('Failed to add expense. Please try again.');
      }
    } catch (error) {
      console.error('Error adding expense:', error);
      alert('An error occurred. Please try again.');
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

  return (
    <div id="group-details-page" style={{ width: '100vw' }}>
      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'flex-start',
          height: '100vh',
          overflow: 'hidden',
          padding: '20px',
          backgroundColor: '#2c2c2c',
          color: '#fff',
        }}
      >
        <h1 style={{ marginBottom: '20px', color: '#fff' }}>Group Details</h1>

        {/* Transactions Section */}
        <div
          style={{
            marginTop: '30px',
            width: '100%',
            maxWidth: '800px',
            backgroundColor: '#3c3c3c',
            padding: '20px',
            borderRadius: '8px',
            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.5)',
          }}
        >
          <h2 style={{ color: '#fff' }}>Your Transactions</h2>
          <ul style={{ listStyleType: 'none', padding: 0 }}>
            {transactions.map((transaction) => (
              <li
                key={transaction.settlementId}
                style={{
                  margin: '10px 0',
                  padding: '10px',
                  border: '1px solid #555',
                  borderRadius: '5px',
                  backgroundColor: '#444',
                  color: '#fff',
                }}
              >
                {transaction.sender === email ? (
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
        </div>


        <h2 style={{ color: '#fff', textAlign: 'center' }}>Expenses</h2>

        {/* Expenses Section */}
        <div
          style={{

            marginTop: '30px',
            width: '100%',
            maxWidth: '800px',
            backgroundColor: '#3c3c3c',
            padding: '20px',
            borderRadius: '8px',
            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.5)',
            marginBottom: '50px',
            overflowY: 'auto',
          }}
        >
          <ul style={{ listStyleType: 'none', padding: 0 }}>
            {expenses.map((expense) => (
              <li
                key={expense.expenseId}
                style={{
                  margin: '10px 0',
                  padding: '10px',
                  border: '1px solid #555',
                  borderRadius: '5px',
                  backgroundColor: '#444',
                  color: '#fff',
                }}
              >
                <p>Paid by: {expense.paidBy}</p>
                <p>Amount: ₹{expense.amount.toFixed(2)}</p>
              </li>
            ))}
          </ul>
        </div>

        {/* Footer with Add Expense Button */}
        <div
          style={{
            position: 'fixed',
            bottom: 0,
            left: 0,
            width: '100%',
            backgroundColor: '#333',
            color: '#fff',
            padding: '10px 20px',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            boxShadow: '0 -2px 5px rgba(0, 0, 0, 0.5)',
          }}
        >
          <button
            onClick={() => setIsPopupOpen(true)}
            style={{
              padding: '10px 20px',
              backgroundColor: '#007bff',
              color: '#fff',
              border: 'none',
              borderRadius: '5px',
              cursor: 'pointer',
              fontWeight: 'bold',
            }}
          >
            Add Expense
          </button>
        </div>
      </div>

      {/* Popup for Adding Expense */}
      {isPopupOpen && (
        <div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 1000,
          }}
        >
          <div
            style={{
              backgroundColor: '#2c2c2c',
              padding: '20px',
              borderRadius: '8px',
              width: '400px',
              boxShadow: '0 4px 8px rgba(0, 0, 0, 0.5)',
              textAlign: 'center',
              color: '#fff',
            }}
          >
            <h2>Add Expense</h2>
            <input
              type="text"
              placeholder="Name"
              value={newExpense.name}
              onChange={(e) => setNewExpense({ ...newExpense, name: e.target.value })}
              style={{
                padding: '10px',
                marginBottom: '10px',
                width: '100%',
                boxSizing: 'border-box',
                backgroundColor: '#444',
                color: '#fff',
                border: '1px solid #555',
                borderRadius: '5px',
              }}
            />
            <input
              type="text"
              placeholder="Description"
              value={newExpense.description}
              onChange={(e) => setNewExpense({ ...newExpense, description: e.target.value })}
              style={{
                padding: '10px',
                marginBottom: '10px',
                width: '100%',
                boxSizing: 'border-box',
                backgroundColor: '#444',
                color: '#fff',
                border: '1px solid #555',
                borderRadius: '5px',
              }}
            />
            <input
              type="number"
              placeholder="Amount"
              value={newExpense.amount}
              onChange={(e) => setNewExpense({ ...newExpense, amount: e.target.value })}
              style={{
                padding: '10px',
                marginBottom: '10px',
                width: '100%',
                boxSizing: 'border-box',
                backgroundColor: '#444',
                color: '#fff',
                border: '1px solid #555',
                borderRadius: '5px',
              }}
            />
            <h3 style={{ color: '#fff' }}>Select Users</h3>
            <ul style={{ listStyleType: 'none', padding: 0, textAlign: 'left' }}>
              {users.map((user) => (
                <li key={user.email} style={{ marginBottom: '10px' }}>
                  <label style={{ color: '#fff' }}>
                    <input
                      type="checkbox"
                      checked={newExpense.selectedUsers.some((u) => u.email === user.email)}
                      onChange={() => handleUserSelection(user)}
                      style={{ marginRight: '10px' }}
                    />
                    {user.name} ({user.email})
                  </label>
                </li>
              ))}
            </ul>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <button
                onClick={handleAddExpense}
                style={{
                  padding: '10px 20px',
                  backgroundColor: '#007bff',
                  color: '#fff',
                  border: 'none',
                  borderRadius: '5px',
                  cursor: 'pointer',
                  fontWeight: 'bold',
                }}
              >
                Save
              </button>
              <button
                onClick={() => setIsPopupOpen(false)}
                style={{
                  padding: '10px 20px',
                  backgroundColor: '#555',
                  color: '#fff',
                  border: 'none',
                  borderRadius: '5px',
                  cursor: 'pointer',
                  fontWeight: 'bold',
                }}
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

export default GroupDetailsPage;