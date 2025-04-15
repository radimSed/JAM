"use strict";

import { pathToServer, Company, CompanyCard, AbstractOperations, MD} from './global.js';

class CompanyOperations extends AbstractOperations{

    constructor(pathToServer) {
        super(pathToServer);
    };

    async create(object){
        let jsonData;
        jsonData = `{ "name":"${object.getName()}" }`;
        
        const queryMethod = "POST";
        const responseData = await fetch(this._pathToServer, {
            method: queryMethod,
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: jsonData,
        });
        this.processResponse(responseData);
    }

    async update(object){
            let jsonData = `{ "id":"${object.getId()}", "name": "${object.getName()}" }`;
            const queryMethod = "PUT";
            const responseData = await fetch(this._pathToServer, {
                method: queryMethod,
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: jsonData,
            });
            this.processResponse(responseData);
    }

    async delete(object){
            const queryMethod = "DELETE";
            const query = `${this._pathToServer}/${object.getId()}`;
            const responseData = await fetch(query, {
                method: queryMethod,
            });
            this.processResponse(responseData);
    }
}

/**
 * class for modal dialog
 */
class ModalDialog extends MD{
    _span;
    _dialog;
    _modalEditBtn;
    _modalDeleteBtn;
    _modalCreateBtn;
    _modalCompId;
    _modalCompName;
    _compOperations;
    _pathToServer;
        
    constructor(span, modalDialog, modalEditBtn, modalDeleteBtn, modalCreateBtn, modalCompId, modalCompName, pathToServer) {
        super();
        this._span = span;
        this._dialog = modalDialog;
        this._modalEditBtn = modalEditBtn;
        this._modalDeleteBtn = modalDeleteBtn;
        this._modalCreateBtn = modalCreateBtn;
        this._modalCompId = modalCompId;
        this._modalCompName = modalCompName;
        this._pathToServer = pathToServer;
        this._compOperations = new CompanyOperations(this._pathToServer);
        this.addListeners();
    }
        
    addListeners() {
        this._span.addEventListener("click", () => {
            this._dialog.style.display = "none";
        });

        this._modalEditBtn.addEventListener("click", () => {
            this._compOperations.update(new Company(this.getId(), this.getName()));
            this.hide();
        });
            
        this._modalDeleteBtn.addEventListener("click", () => {
            this._compOperations.delete(new Company(this.getId(), this.getName()));
            this.hide();
        });

        this._modalCreateBtn.addEventListener('click', () => {
            this._compOperations.create(new Company("", this.getName()));
            this.hide();
        });        
    }
        
    hide() {
        this._dialog.style.display = "none";
    }
        
    display(object, createNew) {
        let company = object;
        if (createNew === true) {
            this._modalDeleteBtn.style.display = "none";
            this._modalEditBtn.style.display = 'none';
            this._modalCreateBtn.style.display = 'block';
        } else {
            this._modalDeleteBtn.style.display = "block";
            this._modalEditBtn.style.display = 'block';
            this._modalCreateBtn.style.display = 'none';
        }
        this._dialog.style.display = "block";
        if (company !== null) {
            this._modalCompId.value = company.getId().toString();
            this._modalCompName.value = company.getName();
        } else {
            this._modalCompId.value = "";
            this._modalCompName.value = "";
        }
    }
        
    getName() {
        return this._modalCompName.value;
    }
        
    getId() {
        return Number(this._modalCompId.value);
    }
}
/***********************************************************************************************
/**
* global variables/constants
*/
//array with results
let resultField = [];
const idInput = document.getElementById("idInput");
const nameInput = document.getElementById("nameInput");
const resultDiv = document.getElementById("results");
const infoDiv = document.getElementById("info");
const cardsContainer = document.getElementById("cardsContainer");
//read buttons
const listBtn = document.querySelector("#listBtn");
const createBtn = document.querySelector("#createBtn");

//modal dialog
const modalDialog = new ModalDialog(document.getElementById("close"), 
                                    document.getElementById("modalDialog"),
                                    document.getElementById("modalEditBtn"),
                                    document.getElementById("modalDeleteBtn"),
                                    document.getElementById("modalCreateBtn"),
                                    document.getElementById("modalIdInput"), 
                                    document.getElementById("modalNameInput"), 
                                    `${pathToServer}comp`)


/**
* gets list of companies according to filters
*/
const getList = async () => {
    let idValue = idInput.value;
    const nameValue = nameInput.value;
    if (idValue.length === 0 || parseInt(idValue) < 0) {
        idValue = "0";
    }
    let query = `${pathToServer}comp?id=${idValue}&name=${nameValue}`;
    const data = await fetch(query).then((response) => response.json());
    
    resultField = []; //clear array of previous results
    data.forEach((portion) => { //create card for each company obtained
        let compCard = new CompanyCard(portion.id, portion.name, portion.card, modalDialog);
        resultField.push(compCard);
    });

    infoDiv.textContent = ""; //clear previous info and show new one
    const info = `Number of retrieved results: ${resultField.length}`;
    infoDiv.innerText = info;
    cardsContainer.textContent = ""; //clear previous results and show new one
    resultField.forEach((card) => {
        card.render(cardsContainer);
    });
};
    
/**
* click listener for List button
*/
listBtn.addEventListener("click", getList);
    
/**
* click listener for Create button
*/
createBtn.addEventListener("click", () => {
    modalDialog.display(null, true);
});

