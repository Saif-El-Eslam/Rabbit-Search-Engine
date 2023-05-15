import React from 'react';
import './SearchResultItem.css';

const SearchResultItem = ({ note }) => {
  return (
    <div className="item">
      <div className="webLink">
        <a className="linkName" href="https://en.wikipedia.org/wiki/Google">
          <h2 className="webTitle">{note.title}</h2>
          {note.linkTitle}
        </a>
      </div>
      <p className="webContent">{note.content}</p>
    </div>
  );
};

export default SearchResultItem;
