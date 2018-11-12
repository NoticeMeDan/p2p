# P2PNode

Node 1 startes uden reference til anden node:  
TCP server socket startes på port  
Der lyttes efter beskeder

Node 1 modtager __CONNECT__ fra Node 2:  
Node 2 sætter front til Node 1'socket port  
Da Node 1 ikke er forbundet med andre nodes sætter den både front og back til at være Node 2'socket port.  
Node 1 sender dernæst en __CONNECT__ til Node 2.  
Node 2 sætter sin back til at være Node 1'socket port.  

Node 2 modtager __CONNECT__ fra Node 3:  
Node 3 sætter front til Node 2'socket port  
Da node 2 allerede har en back, sender den en __SWITCH__ til sin back, med en (Node 2 port, Node 3 port) tuple. Derefter sætter den sin back til Node 3'socket port.  
Node 2'socket back sætter  så sin front til Node 3'socket port i stedet, og sender en __CONNECT__ til Node 3.  
Da Node 3 ikke har en back, sætter den sin back til at være afsenderens port  


Beskeder:  

__CONNECT__:  
Indhold: intet. 
Hvis modtageren hverken har front eller back, sættes begge til afsenderens port, og en __CONNECT__ sendes tilbage afsenderen.
Hvis modtageren har en front, men ikke en back, sættes back til afsenderens port
Hvis modtageren både har en front og en back, sender modtageren en __SWITCH__ til sin egen back, hvorefter den sætter sin back til afsenderens port. 

__SWITCH__:  
Indhold: fromIp|fromPort|toIp|toPort (separeres med pipe)  
Hvis modtageren'socket front matcher fromIp/fromPort, sætter modtageren sin front til toIp/toPort, og sender en __CONNECT__ til toIp/toPort.
Hvis modtageren'socket front ikke matcher fromIp/fromPort, sender modtageren beskeden til sin back.
