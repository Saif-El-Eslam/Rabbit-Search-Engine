import React from 'react';
import SearchResultItem from './SearchResultItem';

const SearchResults = ({ notes }) => {
  return notes.map((note) => (
    <div key={note.id}>
      <SearchResultItem note={note} />
    </div>
  ));
};

export default SearchResults;
