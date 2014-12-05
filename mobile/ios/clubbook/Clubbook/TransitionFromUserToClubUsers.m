//
//  DSLTransitionFromSecondToFirst.m
//  TransitionExample
//
//  Created by Pete Callaway on 21/07/2013.
//  Copyright (c) 2013 Dative Studios. All rights reserved.
//

#import "TransitionFromUserToClubUsers.h"

#import "ClubUsersViewController.h"
#import "UserProfileViewController.h"
#import "ProfileCell.h"

@implementation TransitionFromUserToClubUsers

- (void)animateTransition:(id<UIViewControllerContextTransitioning>)transitionContext {
    UserProfileViewController *fromViewController = (UserProfileViewController*)[transitionContext viewControllerForKey:UITransitionContextFromViewControllerKey];
    ClubUsersViewController *toViewController = (ClubUsersViewController*)[transitionContext viewControllerForKey:UITransitionContextToViewControllerKey];

    UIView *containerView = [transitionContext containerView];
    NSTimeInterval duration = [self transitionDuration:transitionContext];

    // Get a snapshot of the image view
    UIView *imageSnapshot = [fromViewController.imageScrollView snapshotViewAfterScreenUpdates:NO];
    imageSnapshot.frame = [containerView convertRect:fromViewController.imageScrollView.frame fromView:fromViewController.imageScrollView.superview];
    fromViewController.imageScrollView.hidden = YES;

    // Get the cell we'll animate to
    ProfileCell *cell = [toViewController collectionViewCellForThing:fromViewController.user];
    cell.profileAvatar.hidden = YES;

    // Setup the initial view states
    toViewController.view.frame = [transitionContext finalFrameForViewController:toViewController];
    [containerView insertSubview:toViewController.view belowSubview:fromViewController.view];
    [containerView addSubview:imageSnapshot];

    [UIView animateWithDuration:duration animations:^{
        // Fade out the source view controller
        fromViewController.view.alpha = 0.0;

        // Move the image view
        imageSnapshot.frame = [containerView convertRect:cell.profileAvatar.frame fromView:cell.profileAvatar.superview];
    } completion:^(BOOL finished) {
        // Clean up
        [imageSnapshot removeFromSuperview];
        fromViewController.imageScrollView.hidden = NO;
        cell.profileAvatar.hidden = NO;

        // Declare that we've finished
        [transitionContext completeTransition:!transitionContext.transitionWasCancelled];
    }];
}

- (NSTimeInterval)transitionDuration:(id<UIViewControllerContextTransitioning>)transitionContext {
    return 0.3;
}

@end
