import Spinner from './Spinner.jsx'

/**
 * The primary "Explain Code" button. Shows a spinner and disables
 * itself while a request is in flight.
 */
function ExplainButton({ onClick, loading, disabled }) {
  return (
    <button
      className="btn-explain"
      onClick={onClick}
      disabled={loading || disabled}
    >
      {loading ? (
        <>
          <Spinner />
          Explaining...
        </>
      ) : (
        <>Explain Code</>
      )}
    </button>
  )
}

export default ExplainButton
