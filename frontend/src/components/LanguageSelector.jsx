const LANGUAGES = ['Java', 'Python', 'C++', 'JavaScript']

/**
 * Dropdown for choosing which programming language the pasted code is in.
 */
function LanguageSelector({ value, onChange }) {
  return (
    <select
      className="language-select"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      aria-label="Programming language"
    >
      {LANGUAGES.map((lang) => (
        <option key={lang} value={lang}>
          {lang}
        </option>
      ))}
    </select>
  )
}

export default LanguageSelector
