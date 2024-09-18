import {useState} from "react";
import UsernamePage from "./UsernamePage.tsx";
import ChatPage from "./ChatPage.tsx";

function App() {
  const [username, setUsername] = useState<string | null>(null);

  return (
      <div>
        {username ? <ChatPage username={username} /> : <UsernamePage setUsername={setUsername} />}
      </div>
  )
}

export default App;