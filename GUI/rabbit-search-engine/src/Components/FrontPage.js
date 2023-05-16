import React, { useState } from "react";
import { Link } from "react-router-dom";
import "./FrontPage.css";

const FrontPage = () => {
  const [query, setQuery] = useState("");

  const handleSubmit = () => {
    // Handle the search submission logic
  };

  return (
    <div className="google-container">
      <div className="google-logo"></div>
      <div className="google-search">
        <input
          type="text"
          className="search-input"
          placeholder="Search Google"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
        <div className="search-buttons">
          <Link to={`/search?query=${query}`} className="search-link">
            Rabbit Search
          </Link>
        </div>
      </div>
      <div className="google-footer">
        <span className="footer-links">Welcome to Rabbit Search Engine</span>
      </div>
    </div>
  );
};

export default FrontPage;
