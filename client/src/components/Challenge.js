import React, { useState, useEffect } from 'react';
export default function Challenge() {

  const [stock, setStock] = useState([]);

  const getLowStock = async () => {
    console.log("button pressed");
    const response = await fetch("http://localhost:4567/low-stock")
      .then((res) => res.json())
      .then((res) => setStock(res.array))
      .then(console.log(stock));
    //console.log(response);
    //setStock(response.array);
    //console.log(stock);
  }

  
  return (
    <>
      <table>
        <thead>
          <tr>
            <td>SKU</td>
            <td>Item Name</td>
            <td>Amount in Stock</td>
            <td>Capacity</td>
            <td>Order Amount</td>
          </tr>
        </thead>
        <tbody>
          {
            stock.map(item => {
              return(
                <React.Fragment>
                  <tr>
                    <td>{item.id}</td>
                    <td>{item.name}</td>
                    <td>{item.stock}</td>
                    <td>{item.capacity}</td>
                    <td><input></input></td>
                  </tr>
                </React.Fragment>
              )
            })
          /* 
          TODO: Create an <ItemRow /> component that's rendered for every inventory item. The component
          will need an input element in the Order Amount column that will take in the order amount and 
          update the application state appropriately.
          */}
        </tbody>
      </table>
      {/* TODO: Display total cost returned from the server */}
      <div>Total Cost: </div>
      {/* 
      TODO: Add event handlers to these buttons that use the Java API to perform their relative actions.
      */}
      <button onClick = {getLowStock}>
        Get Low-Stock Items
      </button>
      <button>Determine Re-Order Cost</button>
      <div>
      </div>
    </>
  );
}
