import React from "react";
import Highlighter from "react-highlight-words";
import "./SearchResult.css";
import axios from "axios";

function SearchResult({ result, query }) {
  const { title, url, description, keywords } = result;

  const performSearch = (e) => {
    e.preventDefault();
    // replace the query in the URL with the new query
    window.history.pushState(
      {},
      null,
      `http://localhost:3000/search?query=${e.target.innerText}`
    );
    // TODO: reload the page with the new query
    window.location.reload();
  };

  return (
    <div className="search-result">
      {/* if title is not found */}
      {title ? (
        // show the first 15 words of the title
        <a href={"http://" + url} className="search-result__title">
          {title.split(" ").slice(0, 10).join(" ") + "..."}
        </a>
      ) : (
        <a href={"http://" + url} className="search-result__title">
          {url}
        </a>
      )}
      {url.length > 50 ? (
        <p className="search-result__url">{url.slice(0, 50) + "..."}</p>
      ) : (
        <p className="search-result__url">{url}</p>
      )}
      {/* show the first 50 words of the description */}
      <p className="search-result__description">
        <Highlighter
          highlightClassName="highlight"
          searchWords={
            // if query is contained in double quotes, then search for the exact phrase
            query[0] === '"' && query[query.length - 1] === '"'
              ? [query.slice(1, query.length - 1)]
              : query.split(" ")
          }
          autoEscape={true}
          textToHighlight={description + "..."}
        />
      </p>
      {/* map keywords */}
      {keywords ? (
        <p className="search-result__keywords">
          {/* keywords is a list of words, print the first 5 words only */}
          {keywords.slice(0, 5).map((keyword, index) => (
            <span
              key={index}
              className="search-result__keyword"
              onClick={performSearch}
            >
              {keyword}
            </span>
          ))}
        </p>
      ) : null}
    </div>
  );
}

export default SearchResult;
