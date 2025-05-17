import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './Login/login';
import SplitScreenPage from './Home/SplitScreenPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/home" element={<SplitScreenPage />} />
      </Routes>
    </Router>
  );
}

export default App;