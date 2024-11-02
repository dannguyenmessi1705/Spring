import {useEffect, useRef, useState} from "react";
import {Client} from "@stomp/stompjs";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-expect-error
import SockJS from 'sockjs-client/dist/sockjs.js';
import {Box, Button, Container, TextField} from "@mui/material";

import ChatMessage from "./ChatMessage.tsx";

type ChatPageProps = {
  username: string;
}

type Chat = {
  sender: string;
  content: string;
  type: string;
  receiver?: string;
}
function ChatPage({ username } : ChatPageProps) {
  const roomId: string = "testroom";
  const [messages, setMessage] = useState<Chat[]>([]);
  const [client, setClient] = useState<Client | null>(null);
  const [connectionStatus, setConnectionStatus] = useState<string>("Connecting...");
  const messageInputRef = useRef<HTMLInputElement>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const newClient = new Client({ // Tạo một client mới
      webSocketFactory: () => new SockJS(`http://localhost:8080/ws?userId=${username}`), // Kết
      // nối đến
      // server qua
      // websocket
      connectHeaders: {
        "X-User-Id": `${username}`
      },
      onConnect: () => { // Callback được gọi khi kết nối thành công
        console.log("Connected to server");
        const joinMessage: Chat = {
          sender: username,
          type: "CONNECT",
          content: `${username} joined the chat`
        };
        newClient.publish({ // Gửi message lên server
          destination: "/app/chat.add-user",
          body: JSON.stringify(joinMessage)
        });
        newClient.subscribe(`/topic/private/${roomId}`, (message) => { // Đăng ký để nhận
          // message từ
          // server
          const newMessage = JSON.parse(message.body);
          console.log("new message: ", newMessage);
          setMessage(prev => [...prev, newMessage])
        });
        setConnectionStatus("Connected");
      },
      onDisconnect: () => { // Callback được gọi khi client bị ngắt kết nối
        console.log("Disconnected from server");
        if (newClient.connected) {
          const leaveMessage: Chat = {
            sender: username,
            type: "DISCONNECT",
            content: `${username} left the chat`
          };
          newClient.publish({ // Gửi message lên server
            destination: "/app/chat.add-user",
            body: JSON.stringify(leaveMessage)
          });
        }
        setConnectionStatus("Disconnected");
      },
      onWebSocketClose: () => { // Callback được gọi khi websocket bị đóng
        setConnectionStatus('Disconnected');
      },
      onWebSocketError: (error) => { // Callback được gọi khi có lỗi xảy ra với websocket
        console.error('WebSocket error: ', error);
        setConnectionStatus('Failed to connect');
      },
      onStompError: (frame) => { // Callback được gọi khi có lỗi xảy ra với STOMP
        console.log('Broker reported error: ' + frame.headers['message']);
        console.log('Additional details: ' + frame.body);
      },
    });

    newClient.activate(); // Bắt đầu kết nối với server
    setClient(newClient); // Lưu lại client để sử dụng ở các lifecycle hooks khác

    return () => {
      newClient.deactivate(); // Ngắt kết nối khi component bị unmount
    };
  }, [username]);

  useEffect(() => {
    // Scroll to the bottom whenever messages update
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const sendMessage = () => {
    if (messageInputRef.current!.value && client) {
      const chatMessage: Chat = {
        sender: username,
        content: messageInputRef.current!.value,
        type: "CHAT",
        receiver: roomId
      };
      console.log(chatMessage);
      client.publish({
        destination: "/app/chat.send-private-message",
        body: JSON.stringify(chatMessage)
      }); // Gửi message lên server
      messageInputRef.current!.value = ""; // Xóa nội dung trong input sau khi gửi
    } else {
      console.log("Unable to send message");
    }
  };
  const handleKeyDown = (event: React.KeyboardEvent<HTMLDivElement>) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      sendMessage();
      event.preventDefault(); // Prevent form submission
    }
  };


  return (
      <Container>
        <h2>{connectionStatus}</h2>
        <Box display="flex" flexDirection="column" justifyContent="center" alignItems="center" mt={2}>
          <Box sx={{height: '500px', overflow: 'auto', width: '100%'}}>
            {messages.map((message, index) => (
                <ChatMessage key={index} message={message} username={username} />
            ))}
            <div ref={messagesEndRef}/>
          </Box>
          <Box display="flex" justifyContent="center" alignItems="stretch" mt={2}>
            <TextField
                sx={{
                  color: 'white', '& .MuiOutlinedInput-notchedOutline': {borderColor: 'gray'},
                  width: '300px',
                  height: '10px',
                  '& .MuiOutlinedInput-root': {
                    borderRadius: '36px',
                    '& fieldset': {
                      borderColor: 'gray',
                    },
                    '& input': {
                      height: '10px',
                    },
                  },
                }}
                inputProps={{style: {color: 'white'}}}
                inputRef={messageInputRef}
                variant="outlined"
                placeholder="Type a message..."
                onKeyDown={handleKeyDown}
            />
            <Box marginLeft={2}>
              <Button
                  variant="contained"
                  color="primary"
                  sx={{
                    width: '94px',
                    height: '42px',
                    borderRadius: '36px',
                  }}
                  onClick={sendMessage}>
                Send
              </Button>
            </Box>
          </Box>
        </Box>
      </Container>
  )
}

export default ChatPage;