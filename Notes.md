NOTES
===

- CascadeType.PERSIST: When persisting an entity, also persist the entities held in this field. We suggest liberal application of this cascade rule, because if the EntityManager finds a field that references a new entity during flush, and the field does not use CascadeType.PERSIST, it is an error.
- CascadeType.REMOVE: When deleting an entity, also delete the entities held in this field.
- CascadeType.REFRESH: When refreshing an entity, also refresh the entities held in this field.
- CascadeType.MERGE: When merging entity state, also merge the entities held in this field.

http://en.wikibooks.org/wiki/Java_Persistence/ManyToMany

- Patterns for entity removal (@PreRemove): http://blog.xebia.com/2009/04/09/jpa-implementation-patterns-removing-entities/

BUGS
===

- User#removeEvent(): You must remove the event object from db as well! Do it in a service.
- Event#removeRoute(): You must remove the route object from db as well! Do it in a service.
- Route#removeStation(): You must remove the station object from db as well! Do it in a service.
- Route#removeStationManager(): MANAGER MUST BE REMOVED FROM LIST, but also ROUTE MUST BE REMOVED FROM THE USER !!!