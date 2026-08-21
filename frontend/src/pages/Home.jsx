import { useState, useEffect } from 'react'
import LanguageSelector from '../components/LanguageSelector.jsx'
import CodeInput from '../components/CodeInput.jsx'
import ExplainButton from '../components/ExplainButton.jsx'
import ResultCards from '../components/ResultCards.jsx'
import Spinner from '../components/Spinner.jsx'
import { explainCode } from '../services/api.js'

function Home() {
  const [language, setLanguage] = useState('Java')
  const [code, setCode] = useState('')
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const loadingMessages = [
    'Reading your code...',
    'Tracing the logic...',
    'Breaking it down...',
    'Checking for edge cases...',
    'Finding ways to improve it...',
    'Turning code into human language...',
  ]

  const [loadingMessage, setLoadingMessage] = useState(loadingMessages[0])

  useEffect(() => {
    if (!loading) {
      setLoadingMessage(loadingMessages[0])
      return
    }

    let index = 0

    const interval = setInterval(() => {
      index = (index + 1) % loadingMessages.length
      setLoadingMessage(loadingMessages[index])
    }, 2000)

    return () => clearInterval(interval)
  }, [loading])

  const handleExplain = async () => {
    if (!code.trim()) {
      setError('Please paste some code before requesting an explanation.')
      return
    }

    setLoading(true)
    setError('')
    setResult(null)

    try {
      const data = await explainCode(language, code)
      setResult(data)
    } catch (err) {
      const message =
        err.response?.data?.details ||
        err.response?.data?.error ||
        'Could not reach the backend. Is it running on port 8080?'
      setError(message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app">
      <div className="app-header">
        <div className="logo-mark">{'</>'}</div>
        <h1>Decodify</h1>
      </div>
      <p className="app-subtitle">
        Paste any snippet, pick its language, and get a beginner-friendly breakdown
        of what it does, how efficient it is, and how to improve it.
      </p>

      <div className="editor-panel">
        <div className="editor-titlebar">
          <div className="editor-titlebar-left">
            <div className="window-dots">
              <span />
              <span />
              <span />
            </div>
            <span className="editor-titlebar-label">snippet.txt</span>
          </div>
          <LanguageSelector value={language} onChange={setLanguage} />
        </div>

        <CodeInput value={code} onChange={setCode} />

        <div className="editor-footer">
          <span className="char-count">{code.length} characters</span>
          <ExplainButton onClick={handleExplain} loading={loading} />
        </div>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {loading && (
        <div className="loading-row">
          <Spinner />
          <span className="loading-message">{loadingMessage}</span>
        </div>
      )}

      {result && !loading && <ResultCards result={result} />}
    </div>
  )
}

export default Home
