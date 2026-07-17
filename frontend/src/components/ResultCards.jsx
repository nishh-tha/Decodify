import { useState } from 'react'

/**
 * Turns the structured explanation object into a plain-text block,
 * used by the "Copy to Clipboard" button.
 */
function toPlainText(result) {
  const lines = [
    'CODE SUMMARY',
    result.summary,
    '',
    'TIME COMPLEXITY',
    result.timeComplexity,
    '',
    'SPACE COMPLEXITY',
    result.spaceComplexity,
    '',
    'POSSIBLE ISSUES',
    ...result.issues.map((i) => `- ${i}`),
    '',
    'SUGGESTIONS',
    ...result.suggestions.map((s) => `- ${s}`),
  ]
  return lines.join('\n')
}

/**
 * Displays the AI's explanation as a set of cards: summary, time
 * complexity, space complexity, issues, and suggestions.
 */
function ResultCards({ result }) {
  const [copied, setCopied] = useState(false)

  const handleCopy = async () => {
    await navigator.clipboard.writeText(toPlainText(result))
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  return (
    <div className="results">
      <div className="results-header">
        <h2>Explanation</h2>
        <button className="btn-secondary" onClick={handleCopy}>
          {copied ? 'Copied!' : 'Copy to Clipboard'}
        </button>
      </div>

      <div className="card-grid">
        <div className="card span-2">
          <div className="card-label">Code Summary</div>
          <div className="card-body">{result.summary}</div>
        </div>

        <div className="card">
          <div className="card-label">Time Complexity</div>
          <div className="card-body mono">{result.timeComplexity}</div>
        </div>

        <div className="card">
          <div className="card-label">Space Complexity</div>
          <div className="card-body mono">{result.spaceComplexity}</div>
        </div>

        <div className="card issues">
          <div className="card-label">Possible Issues</div>
          <ul>
            {result.issues.map((issue, idx) => (
              <li key={idx}>{issue}</li>
            ))}
          </ul>
        </div>

        <div className="card suggestions">
          <div className="card-label">Suggestions</div>
          <ul>
            {result.suggestions.map((suggestion, idx) => (
              <li key={idx}>{suggestion}</li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  )
}

export default ResultCards
