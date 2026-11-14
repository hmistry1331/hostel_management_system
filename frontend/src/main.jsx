import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import 'react-toastify/dist/ReactToastify.css'
import App from './App.jsx'
import { LanguageProvider } from './store/languageStore'
import { ThemeProvider } from './store/themeStore'
import { ToastContainer } from 'react-toastify'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ThemeProvider>
      <LanguageProvider>
        <App />
        <ToastContainer position="top-right" autoClose={5000} newestOnTop closeOnClick pauseOnFocusLoss />
      </LanguageProvider>
    </ThemeProvider>
  </StrictMode>,
)
