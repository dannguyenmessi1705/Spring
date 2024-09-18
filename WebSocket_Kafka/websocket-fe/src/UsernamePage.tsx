import PropTypes from "prop-types";
import {useState} from "react";
import {Box, Button, Container, TextField} from "@mui/material";

type UsernamePageProps = {
  setUsername: (username: string) => void;
}
function UsernamePage({ setUsername }: UsernamePageProps) {
  UsernamePage.prototype = {
    setUsername: PropTypes.func.isRequired
  }

  const [inputUsername, setInputUsername] = useState<string | null>('');

  const handleUsernameSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (inputUsername) {
      setUsername(inputUsername);
    }
  }

  return (
    <Container>
      <Box display="flex" flexDirection="column" justifyContent="center" alignItems="center" mt={2}>
        <h1>Type your username</h1>
        <form onSubmit={handleUsernameSubmit}>
          <Box display="flex" alignItems="stretch">
            <TextField
              sx={{
                color: 'white', '& .MuiOutlinedInput-notchedOutline': { borderColor: 'gray' },
                width: '300px',
                '& .MuiOutlinedInput-root': {
                  borderRadius: '36px',
                  '& fieldset': {
                    borderColor: 'gray',
                  },
                  '& input': {
                    height: '8px',
                  },
                },
              }}
              inputProps={{ style: { color: 'white' }}}
              variant="outlined"
              placeholder="Username"
              value={inputUsername}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => setInputUsername(e.target.value)}
            />
            <Box marginLeft={2}>
              <Button
                  variant="contained"
                  sx={{
                    width: '94px',
                    height: '42px',
                    borderRadius: '36px',
                  }}
                  color="primary"
                  type="submit">
                Enter
              </Button>
            </Box>
          </Box>
        </form>
      </Box>
    </Container>
  );
}

export default UsernamePage;