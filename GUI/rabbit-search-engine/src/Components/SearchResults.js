import React, { useState, useEffect } from "react";
import SearchResult from "./SearchResult";
import "./SearchResults.css";
//axios
import axios from "axios";

function SearchResults({ query }) {
  const [searchQuery, setSearchQuery] = useState("curious"); // "hello world
  const [results, setResults] = useState([]);

  useEffect(() => {
    // setSearchQuery(query);
    console.log("query", searchQuery);
    //post request, body: {searchQuery}
    axios
      .post("http://localhost:8080", { searchQuery })
      .then((res) => {
        console.log(res.data);
        setResults(res.data);
      })
      .catch((err) => console.log(err));
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    setSearchQuery(e.target.value);
  };

  return (
    <div className="search-results">
      <div className="google-header">
        <div className="google-logo-small">
          <img src="https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png" />
        </div>
        <form className="search-form" onSubmit={handleSearch}>
          <input
            type="text"
            className="search-input"
            placeholder="Search Google"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button type="submit" className="search-button">
            Search
          </button>
        </form>
      </div>
      <div className="results-container">
        <div className="search-results-list">
          {results.map((result) => (
            <SearchResult key={result.id} result={result} />
          ))}
        </div>
        <div className="ads-section">{/* Placeholder for ads section */}</div>
      </div>
    </div>
  );
}

export default SearchResults;
