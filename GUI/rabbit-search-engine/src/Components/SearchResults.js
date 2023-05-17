//
import React, { useState, useEffect } from "react";
import SearchResult from "./SearchResult";
import "./SearchResults.css";
import axios from "axios";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faForward, faBackward } from "@fortawesome/free-solid-svg-icons";
import { Link } from "react-router-dom";

function SearchResults({ query }) {
  const [searchQuery, setSearchQuery] = useState(""); // "hello world
  const [results, setResults] = useState([]);
  const [timeTaken, setTimeTaken] = useState(0);
  const [suggestions, setSuggestions] = useState([]);

  // const numOfPages = Math.ceil(results.length / 10);
  const [numOfPages, setNumOfPages] = useState(0);
  const [page, setPage] = useState(1);
  const [curentPages, setCurrentPages] = useState(results.slice(0, 10));
  const handlenextPage = (e) => {
    if (page < numOfPages) {
      setPage(page + 1);
      setCurrentPages(results.slice(page * 10, page * 10 + 10));
    }
  };
  const handlePrevPage = (e) => {
    if (page > 1) {
      setPage(page - 1);
      setCurrentPages(results.slice((page - 2) * 10, (page - 2) * 10 + 10));
    }
  };

  useEffect(() => {
    // get query from URL
    const q = window.location.search
      .split("=")[1]
      .replace(/%20/g, " ")
      .replace(/%22/g, '"');

    setSearchQuery(q);
    const start = new Date().getTime();

    axios
      .post("http://localhost:8080", q)
      .then((res) => {
        console.log(res.data);
        const jsonData = parseResponse(res.data);
        setResults(jsonData);
        setCurrentPages(jsonData.slice(0, 10));
        setNumOfPages(Math.ceil(jsonData.length / 10));
        setPage(1);
        const end = new Date().getTime();
        setTimeTaken(end - start);
      })
      .catch((err) => console.log(err));
  }, []);

  const handleSearch = (e) => {
    //update the URL
    e.preventDefault();
    window.history.pushState(
      {},
      null,
      `http://localhost:3000/search?query=${searchQuery}`
    );
    // send qwery to server in the body of the request
    const start = new Date().getTime();
    axios
      .post("http://localhost:8080", searchQuery)
      .then((res) => {
        const jsonData = parseResponse(res.data);
        setResults(jsonData);
        setCurrentPages(jsonData.slice(0, 10));
        setNumOfPages(Math.ceil(jsonData.length / 10));
        setPage(1);
        // console.log("jsonData", jsonData);
        const end = new Date().getTime();
        setTimeTaken(end - start);
      })
      .catch((err) => console.log(err));
  };

  const parseResponse = (response) => {
    const regex =
      /Document{{_id=(.*?), url=(.*?), title=(.*?), keywords=(.*?), description=(.*?)}}/g;
    const matches = response.matchAll(regex);
    const jsonData = Array.from(matches).map((match) => ({
      _id: match[1],
      url: match[2],
      title: match[3],
      keywords: match[4],
      description: match[5],
    }));
    //convert keywords to array
    jsonData.forEach((doc) => {
      doc.keywords = doc.keywords.split(" ");
      //remove empty keywords
      doc.keywords = doc.keywords.filter((keyword) => keyword !== "");
    });
    return jsonData;
  };

  const handleTyping = (value) => {
    setSearchQuery(value);
    if (value.length > 0) {
      axios
        .get(`http://localhost:8000/suggestions/${value}`)
        .then((res) => {
          setSuggestions(res.data.slice(1, -1).split(","));
        })
        .catch((err) => console.log(err));
    } else {
      setSuggestions([]);
    }
  };

  return (
    <div className="search-results">
      <div className="google-header">
        <div className="google-logo-small">
          <Link to={`/`}>
            <img src="https://cdn.zapier.com/storage/photos/39bb341a5ebd1e27548b597a555d0bae.png" />
          </Link>
        </div>
        <form className="search-form" onSubmit={handleSearch}>
          <input
            type="text"
            className="search-input"
            placeholder="Search Rabbit"
            value={searchQuery}
            onChange={(e) => handleTyping(e.target.value)}
          />
          <div className="suggestions">
            {suggestions.slice(0, 10).map((suggestion) => (
              <p
                className="suggestion"
                onClick={() => {
                  setSearchQuery(suggestion);
                  setSuggestions([]);
                  //redirect to search results page
                  window.history.pushState(
                    {},
                    null,
                    `http://localhost:3000/search?query=${suggestion}`
                  );
                  // reload the page
                  window.location.reload();
                }}
              >
                {suggestion}
              </p>
            ))}
          </div>

          <button type="submit" className="search-button">
            Search
          </button>
        </form>
      </div>

      <div className="results-container">
        <div className="results-info">
          <p>
            About {results.length} results ({timeTaken / 1000} seconds)
          </p>
        </div>

        <div className="search-results-list">
          {curentPages.map((result) => (
            <SearchResult key={result.id} result={result} query={searchQuery} />
          ))}
        </div>
        <div className="ads-section">{/* Placeholder for ads section */}</div>
      </div>

      <div className="pagination">
        <div className="prev-button">
          <FontAwesomeIcon icon={faBackward} onClick={handlePrevPage} />
        </div>
        <div className="page-numbers">
          <p>{page}</p>
        </div>
        <div className="next-button" onClick={handlenextPage}>
          <FontAwesomeIcon icon={faForward} />
        </div>
      </div>
    </div>
  );
}

export default SearchResults;
