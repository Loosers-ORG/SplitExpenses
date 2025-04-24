import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './Login/login';
import HomePage from './Home/homePage';
import GroupDetailsPage from './Home/GroupDetailsPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/group/:groupId" element= {<GroupDetailsPage />} />
      </Routes>
    </Router>
  );
}

export default App;