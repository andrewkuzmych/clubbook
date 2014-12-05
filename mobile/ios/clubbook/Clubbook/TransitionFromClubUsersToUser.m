//
//  DSLTransitionFromFirstToSecond.m
//  TransitionExample
//
//  Created by Pete Callaway on 21/07/2013.
//  Copyright (c) 2013 Dative Studios. All rights reserved.
//

#import "TransitionFromClubUsersToUser.h"

#import "ClubUsersViewController.h"
#import "UserProfileViewController.h"
#import "ProfileCell.h"


@implementation TransitionFromClubUsersToUser

- (void)animateTransition:(id<UIViewControllerContextTransitioning>)transitionContext {
    ClubUsersViewController *fromViewController = (ClubUsersViewController*)[transitionContext viewControllerForKey:UITransitionContextFromViewControllerKey];
    UserProfileViewController *toViewController = (UserProfileViewController*)[transitionContext viewControllerForKey:UITransitionContextToViewControllerKey];

    UIView *containerView = [transitionContext containerView];
    NSTimeInterval duration = [self transitionDuration:transitionContext];

    // Get a snapshot of the thing cell we're transitioning from
    ProfileCell *cell = (ProfileCell*)[fromViewController.profileCollection cellForItemAtIndexPath:[[fromViewController.profileCollection indexPathsForSelectedItems] firstObject]];
    UIView *cellImageSnapshot = [cell.profileAvatar snapshotViewAfterScreenUpdates:NO];
    cellImageSnapshot.frame = [containerView convertRect:cell.profileAvatar.frame fromView:cell.profileAvatar.superview];
    cell.profileAvatar.hidden = YES;

    // Setup the initial view states
    toViewController.view.frame = [transitionContext finalFrameForViewController:toViewController];
    toViewController.view.alpha = 0;
    toViewController.imageScrollView.hidden = YES;
    toViewController.connectButton.hidden = YES;
    toViewController.connectImage.hidden = YES;

    [containerView addSubview:toViewController.view];
    [containerView addSubview:cellImageSnapshot];

    [UIView animateWithDuration:duration animations:^{
        // Fade in the second view controller's view
        toViewController.view.alpha = 1.0;

        // Move the cell snapshot so it's over the second view controller's image view
        CGRect frame = [containerView convertRect:toViewController.imageScrollView.frame fromView:toViewController.view];
        cellImageSnapshot.frame = frame;
    } completion:^(BOOL finished) {
        // Clean up
        toViewController.imageScrollView.hidden = NO;
        toViewController.connectButton.hidden = NO;
        toViewController.connectImage.hidden = NO;
        cell.hidden = NO;
        [cellImageSnapshot removeFromSuperview];

        // Declare that we've finished
        [transitionContext completeTransition:!transitionContext.transitionWasCancelled];
    }];
}

- (NSTimeInterval)transitionDuration:(id<UIViewControllerContextTransitioning>)transitionContext {
    return 0.3;
}

@end
