import React, { useState } from 'react';
export default function Challenge() {

  const [stock, setStock] = useState([]);
  const [totalCost, setTotalCost] = useState([]);
  const [products, setProducts] = useState(new Map([]));

  const getLowStock = async () => {
    await fetch("http://localhost:4567/low-stock")
      .then((res) => res.json())
      .then((res) => setStock(res.array))
      .then(console.log(stock));
  }

  const getTotalCost = async () => {
    const json = JSON.stringify(Object.fromEntries(products));
    await fetch("http://localhost:4567/restock-cost",{
      method: 'POST',
      body: json,
    })
      .then((res) => res.json())
      .then((res) => setTotalCost(res.cost));
  }

  const updateProductMap = (name, amount) => {
    setProducts(new Map(products.set(name, Number(amount))));
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
                    <td><input onChange={e => updateProductMap(item.name, e.target.value)}></input></td>
                  </tr>
                </React.Fragment>
              )
            })
          }
        </tbody>
      </table>
      <div>Total Cost: ${Math.round(totalCost * 100)/ 100} </div>
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
