import axios from 'axios'

// Base URL of the Spring Boot backend
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

/**
 * Sends the code + language to the backend and returns the
 * structured explanation object.
 *
 * @param {string} language - e.g. "Java"
 * @param {string} code - the source code to explain
 * @returns {Promise<object>} the explanation response
 */
export async function explainCode(language, code) {
  const response = await axios.post(`${API_BASE_URL}/explain`, {
    language,
    code,
  })
  return response.data
}
