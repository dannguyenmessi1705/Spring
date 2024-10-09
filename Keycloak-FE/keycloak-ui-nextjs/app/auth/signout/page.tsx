import { authOptions } from "@/app/api/auth/[...nextauth]/route";
import Logout from "@/components/Logout";
import { getServerSession } from "next-auth";
import { redirect } from "next/navigation";

export default async function Singout() {
  const session = await getServerSession(authOptions);
  if (session) {
    return (
      <div className="flex h-screen flex-col items-center justify-center space-y-3">
        <div className="text-xl font-bold">Signout</div>
        <div>Are you sure you want to sign out?</div>
        <div>
          <Logout />
        </div>
      </div>
    );
  }
  return redirect("/api/auth/signin");
}
