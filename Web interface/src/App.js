import React from 'react';
// import SearchBar from './Components/SearchBar';
import SearchBox from './Components/SearchBox';
import './App.css';
import SearchResults from './Components/SearchResults';

function App({ props }) {
  return (
    <div className="App">
      <SearchBox />
      <SearchResults notes={props} />
    </div>
  );
}

export default App;
