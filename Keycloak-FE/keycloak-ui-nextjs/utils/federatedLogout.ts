import { signOut } from "next-auth/react";

export default async function federatedLogout() {
  try {
    const response = await fetch("/api/auth/federated-logout");
    const data = await response.json();
    if (response.ok) {
      await signOut({ redirect: false });
      window.location.href = data.url;
      return;
    }
    throw new Error(data.error || "Failed to federated logout");
  } catch (error) {
    console.error("Failed to federated logout", error);
    await signOut({ redirect: false });
    window.location.href = "/";
  }
}
