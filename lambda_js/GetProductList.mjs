const mockProducts = [
  {
    "count": 4,
    "description": "Short Product Description1",
    "id": "7567ec4b-b10c-48c5-9345-fc73c48a80aa",
    "price": 2.4,
    "title": "ProductOne"
  },
  {
    "count": 6,
    "description": "Short Product Description3",
    "id": "7567ec4b-b10c-48c5-9345-fc73c48a80a0",
    "price": 10,
    "title": "ProductNew"
  }];

export const handler = async (event) => {
  console.log("Incoming event ", event);

  console.log("Mocked Products ", mockProducts);
  const response = {
    statusCode: 200,
    body: JSON.stringify(mockProducts),
  };
  return response;
};