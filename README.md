# State_machine_replicator
protocole de réplication de machine à états

# Instaltation avec eclipse
```
wget http://downloads.sourceforge.net/project/peersim/peersim-1.0.5.zip

unzip peersim-1.0.5.zip

sudo cp peersim-1.0.5/*.jar Path jusque/SMR/lib 

```
# configuration eclipse

- Run -> Run configurations
- choisir Java Application
- new launch configuration 
- Search dans main class
- choisir Simulator
- appuier sur Arguments 
- dans Program arguments appuier sur variables
- ajouter le path ver le fichier de conf

# Model 

- n’importe quel nœud du système peut tomber en panne n’importe quand

- une panne peut être définitive ou temporaire

- un message peut être perdu ou dupliqué

- les canaux de communications ne sont pas FIFO

Nous considéron un eensemble de noeuds hhébergeant les réplicas noté R 

Chaque noeud R :
- est identifier par un indice i telque 0 <= i < |R|. || = ensemble ?

- Tous ri ∈ R a une historique Hi ordonnais , persistant  et stocker localment et est fiable

- L’indice d’une requête dans un historique est appelé itération >= 0

- si deux itérations x et y existent dans H et que x < y alors H(x) désigne une
requête qui a été délivrée avant H(y)

- Propriété de surté a respecter pour  ∀ Hi et ∀x ∈ Hi:
 
- soit H i (x) = ⊥ signifiant que le réplica r i ne connait pas encore la valeur de la
requête associée à l’itération x

- soit H i (x) = req tel que ∀H j 6 = H i ∧H j (x) 6 = ⊥ ⇒ H j (x) = req signifiant que tout
autre historique qui maintient une valeur différente de ⊥ à l’itération x implique
que la valeur de cette itération doit être égale à req.


# Model de Paxos

### Nœuds :
- tout nœud interne au système est à la fois Proposer, Acceptor
et Learner

- tout nœud n ∈ Π a le rôle de Proposer autrement dit éligible à l’élection de leader

- tout nœud n ∈ Π a le rôle d’Acceptor, donc pour avoir
une majorité est de recevoir N 2 + 1 message Promise et Accepted contenant la
même valeur pour pouvoir passer en phase 2 et 3

- tout nœud n ∈ Π a le rôle de Learner impliquant qu’il y a N réplica du service
(donc N historique) dans le système. À la validation d’une nouvelle itération x
dans son historique H, avant d’exécuter la requête et répondre au client, un learner
devra s’assurer qu’il n’y ait pas d’itération y < x tel que H(y) = ⊥. Ceci peut se
produire si le noeud est tombé en panne puis a redémarré ou bien si des messages
Accepted ont été perdus. Si c’est le cas, il faudra relancer un round Paxos (via le
leader) pour connaître les valeurs manquantes.