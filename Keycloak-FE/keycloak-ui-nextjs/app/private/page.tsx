import { getServerSession } from "next-auth";
import Logout from "@/components/Logout";
import { authOptions } from "../api/auth/[...nextauth]/route";

export default async function Private() {
  const session = await getServerSession(authOptions);
  if (session) {
    return (
      <div className="flex h-screen flex-col items-center justify-center space-y-3">
        <div>You are accessing a private page</div>
        <div>Your name is {session.user?.name}</div>
        <div>Your token is {session.accessToken}</div>
        <div>Your email is {session.user?.email}</div>
        <div>
          <Logout />
        </div>
      </div>
    );
  }
}
