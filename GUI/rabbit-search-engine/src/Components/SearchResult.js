import React from "react";
import "./SearchResult.css";

function SearchResult({ result }) {
  const { title, url, description } = result;

  return (
    <div className="search-result">
      <a href={url} className="search-result__title">
        {title}
      </a>
      <p className="search-result__url">{url}</p>
      <p className="search-result__description">{description}</p>
    </div>
  );
}

export default SearchResult;
