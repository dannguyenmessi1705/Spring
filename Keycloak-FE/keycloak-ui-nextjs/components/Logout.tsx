"use client";
import federatedLogout from "@/utils/federatedLogout";

export default function Logout() {
  return (
    <button
      onClick={() => federatedLogout()}
      className="rounded-full bg-sky-500 px-5 py-2 text-sm font-semibold leading-5 text-white hover:bg-sky-700"
    >
      SignOut with Keycloak
    </button>
  );
}
