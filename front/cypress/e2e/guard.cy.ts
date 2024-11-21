import { cyUtils } from './utils-cy';
import { User } from '../../src/app/interfaces/user.interface';

describe('Forbid when logged out', () => {
  it('should not allow sessions page', () => {
    cy.visit('/sessions');
    cy.url().should('contain', '/login');
  });

  it('should not allow me page', () => {
    cy.visit('/me');
    cy.url().should('contain', '/login');
  });

  it('should allow home page', () => {
    cy.visit('/');
    cy.url().should('contain', '/');
  });
});

describe('Allow and redirect when logged in', () => {
  const user: User = cyUtils.user(1);
  it('should redirect to Session Page', () => {
    cy.visit('/login');
    cyUtils.login(user);
    cy.url().should('contain', '/sessions');
  });
});
