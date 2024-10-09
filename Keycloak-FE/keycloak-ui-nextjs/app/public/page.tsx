import { getServerSession } from "next-auth";
import { authOptions } from "../api/auth/[...nextauth]/route";
import Logout from "@/components/Logout";
import Login from "@/components/Login";

export default async function Public() {
  const session = await getServerSession(authOptions);
  if (session) {
    return (
      <div className="flex h-screen flex-col items-center justify-center space-y-3">
        <div>You are accessing a public page</div>
        <div>Your name is {session.user?.name}</div>
        <div>Your token is {session.accessToken}</div>
        <div>Your email is {session.user?.email}</div>
        <div>
          <Logout />
        </div>
      </div>
    );
  }
  return (
    <div className="flex h-screen flex-col items-center justify-center space-y-3">
      <div>You are accessing a public page</div>
      <div>
        <Login />
      </div>
    </div>
  );
}
