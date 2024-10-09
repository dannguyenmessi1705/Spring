import { useKeycloak } from "@react-keycloak/web";

export function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { keycloak } = useKeycloak();
  function Login() {
    keycloak.login();
    return null;
  }
  console.log("keycloak.authenticated", keycloak.authenticated);
  return keycloak.authenticated ? children : <Login />;
}
