import { cyUtils } from './utils-cy';
import { User } from '../../src/app/interfaces/user.interface';

const user: User = cyUtils.user(1);
const admin: User = cyUtils.user(2, true);

describe('Account Page', () => {
  beforeEach(() => {
    cyUtils.expectSessionList();
  });

  it('Should show the user information as user', () => {
    cyUtils.login(user);
    cy.wait('@sessionList');
    cyUtils.expectUserById(user);
    cy.get('#accountButton').click();
    cy.url().should('contain', '/me');
    cy.wait('@userById');
    cy.get('h1').should('contain.text', 'User information');

    cy.get('div p')
      .eq(0)
      .should(
        'contain.text',
        `Name: ${user.firstName} ${user.lastName.toUpperCase()}`,
      );
    cy.get('div p').eq(1).should('contain.text', `Email: ${user.email}`);
    cy.get('div p').eq(2).should('contain.text', 'Delete my account:');
    cy.get('button span').should('contain.text', 'Delete');
  });

  it('Should show the user information as admin', () => {
    cyUtils.login(admin);
    cy.wait('@sessionList');
    cyUtils.expectUserById(admin, 'adminById');
    cy.get('#accountButton').click();
    cy.url().should('contain', '/me');
    cy.wait('@adminById');

    cy.get('div p')
      .eq(0)
      .should(
        'contain.text',
        `Name: ${admin.firstName} ${admin.lastName.toUpperCase()}`,
      );
    cy.get('div p').eq(1).should('contain.text', `Email: ${admin.email}`);
    cy.get('div p').eq(2).should('contain.text', 'You are admin');
  });
});

describe('Account deletion & logout', () => {
  beforeEach(() => {
    cyUtils.expectSessionList();
    cyUtils.login(user);
    cy.wait('@sessionList');
    cyUtils.expectUserById(user);
  });

  it('should delete the account', () => {
    cy.get('#accountButton').click();
    cy.url().should('contain', '/me');
    cy.wait('@userById');
    cy.intercept('DELETE', '/api/user/1', {
      statusCode: 200,
    }).as('userDelete');
    cy.get('button span .ml1').first().should('contain.text', 'Delete').click();
    cy.wait('@userDelete');
    cy.get('#login').should('exist');
  });

  it('should log the user out', () => {
    cy.get('#logout').should('exist').click();
  });
});
