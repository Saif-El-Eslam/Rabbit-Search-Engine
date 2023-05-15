import React, { useState } from 'react';
import './SearchBar.css';

const SearchBar = () => {
  const [searchInput, setSearchInput] = useState('');

  const handleChange = (e) => {
    e.preventDefault();
    setSearchInput(e.target.value);
  };

  if (searchInput.length > 0) {
    //fetch from server
    console.log(searchInput);
  }

  return (
    <div>
      <input //specifies an input field where the user can enter data
        type="text"
        placeholder="Search here"
        onChange={handleChange}
        className="SearchBar"
        value={searchInput}
      />

      <button className="myButton" variant="contained">
        Search
      </button>
    </div>
  );
};

export default SearchBar;
