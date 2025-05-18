import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './Login/login';
import SplitScreenPage from './Home/SplitScreenPage';
import './App.css'; // Ensure global styles are applied

function App() {
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Router>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/home" element={<SplitScreenPage />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;