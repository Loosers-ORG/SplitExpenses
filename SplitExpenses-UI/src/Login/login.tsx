import React, { useState } from 'react';
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import { useNavigate } from 'react-router-dom';
import styles from './login.module.css';

function Login() {
  const [user, setUser] = useState(null); // State to store logged-in user info
  const [isDarkMode, setIsDarkMode] = useState(false); // State to toggle dark mode
  const navigate = useNavigate(); // React Router's navigation hook

  const handleGoogleLoginSuccess = (credentialResponse) => {
    const decodedToken = JSON.parse(atob(credentialResponse.credential.split('.')[1]));
    setUser({
      email: decodedToken.email,
      name: decodedToken.name,
      picture: decodedToken.picture,
    });

    // Add user to the database
    const userData = {
      email: decodedToken.email,
      name: decodedToken.name,
    };

    fetch('http://localhost:8080/user', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData),
    });

    // Navigate to the home page with the user's email as a query parameter
    navigate(`/home?email=${decodedToken.email}`);
  };

  const handleGoogleLoginError = () => {
    console.error('Google Login Failed');
  };

  const toggleDarkMode = () => {
    setIsDarkMode(!isDarkMode);
  };

  return (
    <GoogleOAuthProvider clientId="998575087605-8kg6g4slrh5c53shqupfqcfmn60u0244.apps.googleusercontent.com">
      <div className={`${styles.container} ${isDarkMode ? styles.dark : ''}`}>
        {/* Dark Mode Toggle Button */}
        <button className={styles.toggleButton} onClick={toggleDarkMode}>
          {isDarkMode ? '🌙' : '☀️'}
        </button>

        {!user ? (
          <div className={styles.googleLoginContainer}>
            <h1>Login with Google</h1>
            <GoogleLogin
              onSuccess={handleGoogleLoginSuccess}
              onError={handleGoogleLoginError}
            />
          </div>
        ) : (
          <div className={styles.googleProfile}>
            <h1>Welcome, {user.name}</h1>
            <img src={user.picture} alt="User Profile" />
            <p>{user.email}</p>
          </div>
        )}
      </div>
    </GoogleOAuthProvider>
  );
}

export default Login;