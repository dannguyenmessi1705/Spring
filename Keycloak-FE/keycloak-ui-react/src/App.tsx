import { useState } from "react";
import "./App.css";
import Keycloak from "keycloak-js";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
  NavLink,
} from "react-router-dom";
import Home from "./Home";
import About from "./About";
import { PrivateRoute } from "./PrivateRoute";

const keycloak = new Keycloak({
  url: "http://localhost:7081",
  realm: "master",
  clientId: "dan",
});
const initOptions: Keycloak.KeycloakInitOptions = {
  pkceMethod: "S256",
  checkLoginIframe: false,
  onLoad: "login-required",
};
function App() {
  const [count, setCount] = useState(0);

  return (
    <ReactKeycloakProvider
      authClient={keycloak}
      initOptions={initOptions}
      LoadingComponent={<div>Loading...</div>}
      onEvent={(event, error) => {
        console.log("onKeycloakEvent", event, error);
      }}
      onTokens={(tokens) => {
        console.log("onKeycloakTokens", tokens);
      }}
      autoRefreshToken={true}
    >
      <Router>
        <div>
          <nav>
            <ul>
              <li>
                <NavLink to="/home">Home</NavLink>
              </li>
              <li>
                <NavLink to="/about">About</NavLink>
              </li>
              <li>
                <div>
                  <button onClick={() => keycloak.logout()}>Logout</button>
                </div>
              </li>
            </ul>
          </nav>
        </div>
        <Routes>
          <Route path="/" element={<Navigate to="/home" />} />
          <Route
            path="/home"
            element={
              <PrivateRoute>
                <Home count={count} setCount={setCount} />
              </PrivateRoute>
            }
          />
          <Route
            path="/about"
            element={
              <PrivateRoute>
                <About />
              </PrivateRoute>
            }
          />
          <Route
            path="/logout"
            element={
              <div>
                <button onClick={() => keycloak.logout()}>Logout</button>
              </div>
            }
          />
        </Routes>
      </Router>
    </ReactKeycloakProvider>
  );
}

export default App;
