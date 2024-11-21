import { Session } from '../../src/app/features/sessions/interfaces/session.interface';
import * as _ from 'lodash';
import { Teacher } from '../../src/app/interfaces/teacher.interface';
import { User } from '../../src/app/interfaces/user.interface';

const login = (user: User) => {
  cy.visit('/login');
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      id: user.id,
      username: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      admin: user.admin,
    },
  });

  cy.get('input[formControlName=email]').type('yoga@studio.com');
  cy.get('input[formControlName=password]').type(`test!1234{enter}{enter}`);
};

const yogaSession = (participantsCount: number[]): Session => {
  return {
    id: 1,
    teacher_id: _.random(2),
    createdAt: new Date('2024-10-29T17:51:57'),
    date: new Date('2024-10-29T16:52:03.000+00:00'),
    description: `this is a long description`,
    name: `Session`,
    updatedAt: new Date('2024-10-29T17:52:23'),
    users: participantsCount,
  };
};

const teacher = (id: number): Teacher => {
  return {
    id: id,
    firstName: 'firstname' + id,
    lastName: 'lastname' + id,
    createdAt: new Date(),
    updatedAt: new Date(),
  };
};

const user = (id: number, isAdmin: boolean = false): User => {
  return {
    id: id,
    email: 'email@email.com' + id,
    firstName: 'firstname' + id,
    lastName: 'lastname' + id,
    createdAt: new Date(),
    updatedAt: new Date(),
    admin: isAdmin,
    password: 'password' + id,
  };
};

const yogaSessions = (number: number) => {
  const sessions = [];

  for (let i = 0; i < number; i++) {
    sessions.push({
      id: i,
      teacher_id: _.random(2),
      createdAt: '2024-10-29T17:51:57',
      date: '2024-10-29T16:52:03.000+00:00',
      description: `description ${i}`,
      name: `Session number ${i + 1}`,
      updatedAt: '2024-10-29T17:52:23',
      users: [],
    });
  }

  return sessions;
};

const expectSessionList = () => {
  cy.intercept('GET', '/api/session', {
    statusCode: 200,
    body: yogaSessions(10),
  }).as('sessionList');
};

const expectUserById = (user: User, as: string = 'userById') => {
  cy.intercept('GET', '/api/user/' + user.id, {
    statusCode: 200,
    body: user,
  }).as(as);
};

export const cyUtils = {
  login,
  yogaSession,
  yogaSessions,
  teacher,
  expectSessionList,
  expectUserById,
  user,
};
