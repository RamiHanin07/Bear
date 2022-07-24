import React, { useState, useEffect } from 'react';
export default function Challenge() {

  const [stock, setStock] = useState([]);
  const [totalCost, setTotalCost] = useState([]);
  const [products, setProducts] = useState(new Map([]));

  const getLowStock = async () => {
    console.log("getting low stock items");
    const response = await fetch("http://localhost:4567/low-stock")
      .then((res) => res.json())
      .then((res) => setStock(res.array))
      .then(console.log(stock));
  }

  const getTotalCost = async () => {
    console.log("getting total cost");
    
    const json = JSON.stringify(Object.fromEntries(products));
    console.log(json);
    const response = await fetch("http://localhost:4567/restock-cost",{
      method: 'POST',
      body: json,
    })
      .then((res) => res.json())
      .then((res) => setTotalCost(res.cost))
      .then(console.log(totalCost));
  }

  const updateProductMap = (name, amount) => {
    setProducts(new Map(products.set(name, Number(amount))));
    console.log("name: " + name + " ; amount: " + amount);
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
          /* 
          TODO: Create an <ItemRow /> component that's rendered for every inventory item. The component
          will need an input element in the Order Amount column that will take in the order amount and 
          update the application state appropriately.
          */
            stock.map(item => {
              
              
              return(
                <React.Fragment>
                  <tr>
                    <td>{item.id}</td>
                    <td>{item.name}</td>
                    <td>{item.stock}</td>
                    <td>{item.capacity}</td>
                    <td><input onChange={e => updateProductMap(item.name, e.target.value)}></input></td>
                  </tr>
                </React.Fragment>
              )
            })
          }
        </tbody>
      </table>
      {/* TODO: Display total cost returned from the server */}
      <div>Total Cost: ${Math.round(totalCost * 100)/ 100} </div>
      
      {/* 
      TODO: Add event handlers to these buttons that use the Java API to perform their relative actions.
      */}
      <button onClick = {getLowStock}>
        Get Low-Stock Items
      </button>
      <button onClick = {getTotalCost}>
        Determine Re-Order Cost</button>
      <div>
      </div>
    </>
  );
}
