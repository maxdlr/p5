import _ from 'lodash';

const login = () => {
  cy.visit('/login')
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      id: 1,
      username: 'userName',
      firstName: 'firstName',
      lastName: 'lastName',
      admin: true
    },
  })

  cy.get('input[formControlName=email]').type("yoga@studio.com")
  cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
}

const yogaSessions = (number: number) => {
  const sessions = [];

  for (let i = 0; i < number; i++) {
    sessions.push({
      id: i,
      teacher_id: _.random(2),
      createdAt: "2024-10-29T17:51:57",
      date: "2024-10-29T16:52:03.000+00:00",
      description: `description ${i}`,
      name: `Session number ${i + 1}`,
      updatedAt: "2024-10-29T17:52:23",
      users: [],
    })
  }

  return sessions;
}

describe("Session list", () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: yogaSessions(10),
    }).as('sessionList')
    login()
  })

  it('should show the session list', () => {
    cy.wait('@sessionList');

    cy.get('div.items').should('be.visible');
    cy.get('mat-card.item').should('have.length', 10);
    cy.get('mat-card.item button .ml1').first().should('contain.text', 'Detail')
    cy.get('mat-card.item button .ml1').eq(1).should('contain.text', 'Edit')

  });
})
