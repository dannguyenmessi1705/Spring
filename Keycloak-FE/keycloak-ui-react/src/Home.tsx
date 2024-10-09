import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";

export default function Home({
  count,
  setCount,
}: {
  count: number;
  setCount: (count: number | ((prev: number) => number)) => void;
}) {
  return (
    <div>
      <header className="App-header">
        <img src={reactLogo} className="App-logo" alt="logo" />
        <img src={viteLogo} className="App-logo" alt="logo" />
        <p>
          Edit <code>App.tsx</code> and save to reload.
        </p>
        <p>
          <button onClick={() => setCount((count) => count + 1)}>
            count is: {count}
          </button>
        </p>
      </header>
    </div>
  );
}
