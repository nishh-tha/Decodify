/**
 * Large textarea where the user pastes the code they want explained.
 */
function CodeInput({ value, onChange }) {
  return (
    <textarea
      className="code-textarea"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      placeholder={'// Paste your code here...\n\nfunction example() {\n  return "Hello, world!";\n}'}
      spellCheck="false"
    />
  )
}

export default CodeInput
