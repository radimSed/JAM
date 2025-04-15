export const pathToServer = "http://localhost:8080/JAM/"

/**
 * class for objects containing data of the received company
 */
export class Company {
    _id;
    _name;
    constructor(id, name) {
        this._id = id;
        this._name = name;
    }
    getId() {
        return this._id;
    }
    getName() {
        return this._name;
    }
}

/**
 * class for objects containing data of the received company
 */
export class CompanyCard extends Company {
    _card;
    _dialog;
    
    constructor(id, name, card, modalDialog) {
        super(id, name);
        this._card = card;
        this._dialog = modalDialog;
    }

    /**
     * shows cards of the received companies. On click --> show modal dialog for possible changes
     * @param whereTo
     */
    render(whereTo) {
        const cardDiv = document.createElement("div");
        cardDiv.classList.add("cardDiv");
        cardDiv.innerHTML = this._card;
        cardDiv.addEventListener("click", () => {
            this._dialog.display(new Company(this.getId(), this.getName()), false);
        });
        whereTo.appendChild(cardDiv);
    }
}

export class Person {
    _id;
    _name;
    _email;
    _phone;
        
    constructor(id, name, email, phone) {
        this._id = id;
        this._name = name;
        this._email = email;
        this._phone = phone;
    }

    getId(){
        return this._id;
    }

    getName(){
        return this._name;
    }

    getEmail(){
        return this._email;
    }

    getPhone(){
        return this._phone;
    }
}

export class PersonCard extends Person{
    _card;
    _dialog;
        
    constructor(id, name, email, phone, card, modalDialog) {
        super(id, name, email, phone)
        this._card = card;
        this._dialog = modalDialog;
    }
        
    /**
    * shows cards of the received companies. On click --> show modal dialog for possible changes
    * @param whereTo
    */
    render(whereTo) {
        const cardDiv = document.createElement('div');
        cardDiv.classList.add('cardDiv');
        cardDiv.innerHTML = this._card;
        cardDiv.addEventListener('click', () => {
            this._dialog.display(new Person(this.getId(), this.getName(), this.getEmail(), this.getPhone()), false);
        });
        whereTo.appendChild(cardDiv);
    }
}

/**
 * abstract class for operations
 */
export class AbstractOperations {
    _pathToServer;

    constructor(pathToServer) {
        this._pathToServer = pathToServer;
    };
    
    async create(object) {};
    async update(object) {};
    async delete(object) {};

    async processResponse(response) {
        if (response.ok) {
            const resultMessage = (await response.json());
            alert(resultMessage.message);
        }
        else {
            const resultMessage = (await response.json());
            alert(resultMessage.message);
        }
    }
}

/**
 * abstract class for modal dialogs
 */
export class MD{
    display(object, createNew) {}
}

