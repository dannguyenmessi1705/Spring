"use client";
import { minutesInSeconds } from "@/utils/time";
import { SessionProvider } from "next-auth/react";
import { ReactNode } from "react";

export function Providers({ children }: { children: ReactNode }) {
  return (
    <SessionProvider refetchInterval={minutesInSeconds(4)}>
      {children}
    </SessionProvider>
  );
}
