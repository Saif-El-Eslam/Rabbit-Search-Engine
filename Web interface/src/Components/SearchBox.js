import React from 'react';
import SearchBar from './SearchBar';
import './SearchBox.css';
import pic from '../icons8-search-96.png';

const SearchBox = () => {
  return (
    <div className="SearchBox">
      <img src={pic} alt="Search icon" />
      <SearchBar />
    </div>
  );
};

export default SearchBox;
