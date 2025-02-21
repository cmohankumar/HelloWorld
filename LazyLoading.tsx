import React, { useState, useRef } from 'react';

function MyComponent() {
  const [selectedOption, setSelectedOption] = useState('');
  const [loading, setLoading] = useState(false);
  const timeoutId = useRef(null);

  const handleOptionChange = (event) => {
    const value = event.target.value;
    setSelectedOption(value);

    clearTimeout(timeoutId.current);

    if (value === 'no') {
      timeoutId.current = setTimeout(() => {
        setLoading(true);
        fetchData().finally(() => setLoading(false));
      }, 3000);
    }
  };

  const fetchData = async () => {
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 2000));
      console.log('API call completed');
    } catch (error) {
      console.error('API call failed', error);
    }
  };

  return (
    <div>
      <label>
        <input
          type="radio"
          value="yes"
          checked={selectedOption === 'yes'}
          onChange={handleOptionChange}
        />
        Yes
      </label>
      <label>
        <input
          type="radio"
          value="no"
          checked={selectedOption === 'no'}
          onChange={handleOptionChange}
        />
        No
      </label>

      {loading && <div>Loading...</div>}
    </div>
  );
}

export default MyComponent;

/*
Yes, the debounce approach will still work with an async function for the API call. However, you'll need to make a few adjustments to handle the asynchronous nature of the API call. Here's how you can modify the implementation to work with an async function:

Create an async function for your API call:

javascript*/
const makeApiCall = async () => {
  try {
    // Your async API call logic here
    const response = await fetch('your-api-endpoint');
    const data = await response.json();
    console.log('API call successful:', data);
  } catch (error) {
    console.error('API call failed:', error);
  }
};
/*Create a debounced wrapper for the async function:

javascript*/
import { debounce } from 'lodash';

const debouncedApiCall = debounce(() => {
  makeApiCall();
}, 3000);
/*Use the debounced function in your radio button change handler:

javascript*/
const handleRadioChange = (event) => {
  const newSelection = event.target.value;
  setSelection(newSelection);

  if (newSelection === 'no') {
    debouncedApiCall();
  } else {
    debouncedApiCall.cancel();
  }
};
/*
This approach will work because:

The debounce function wraps the call to makeApiCall(), not the async function itself.

When the debounced function is finally executed, it will call makeApiCall(), which will then execute asynchronously.

If the user changes their selection before the debounce period ends, the cancel() method will prevent the API call from being made.

Remember to handle any state updates or side effects that depend on the API call result within the makeApiCall function or use appropriate state management techniques (like React Query or Redux) for more complex scenarios.*/