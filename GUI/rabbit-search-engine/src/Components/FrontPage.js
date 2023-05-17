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
      <div className="google-logo">
        <img src="https://cdn.zapier.com/storage/photos/39bb341a5ebd1e27548b597a555d0bae.png" />
      </div>
      <div className="google-search">
        <input
          type="text"
          className="search-input"
          placeholder="Search Rabbit"
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
