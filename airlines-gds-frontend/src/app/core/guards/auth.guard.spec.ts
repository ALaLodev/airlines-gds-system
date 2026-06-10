import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('authGuard', () => {
  let mockAuthService: any;
  let mockRouter: any;
  let navigateSpyCalledWith: any[] | null = null;

  beforeEach(() => {
    navigateSpyCalledWith = null;
    mockAuthService = {
      isLoggedIn: () => false
    };
    mockRouter = {
      navigate: (commands: any[]) => {
        navigateSpyCalledWith = commands;
        return Promise.resolve(true);
      }
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    });
  });

  it('should return true if the user is logged in', () => {
    mockAuthService.isLoggedIn = () => true;

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

    expect(result).toBe(true);
    expect(navigateSpyCalledWith).toBeNull();
  });

  it('should redirect to /login and return false if the user is not logged in', () => {
    mockAuthService.isLoggedIn = () => false;

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

    expect(result).toBe(false);
    expect(navigateSpyCalledWith).toEqual(['/login']);
  });
});
