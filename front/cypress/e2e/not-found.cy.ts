describe('Not found Page', () => {
  it('Should catch the bad url and show not found page', () => {
    cy.visit('/etrsfdsdgf');
    cy.get('h1').should('contain.text', 'Page not found !');
  });
});
