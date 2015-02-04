//
//  TransitionFromClubListToClub.m
//  Clubbook
//
//  Created by Andrew on 10/11/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "TransitionFromClubListToClub.h"

#import "PlacesViewController.h"
#import "ClubUsersViewController.h"
#import "ClubCell.h"

@implementation TransitionFromClubListToClub
- (void)animateTransition:(id<UIViewControllerContextTransitioning>)transitionContext {
    PlacesViewController *fromViewController = (PlacesViewController*)[transitionContext viewControllerForKey:UITransitionContextFromViewControllerKey];
    ClubUsersViewController *toViewController = (ClubUsersViewController*)[transitionContext viewControllerForKey:UITransitionContextToViewControllerKey];
    
    UIView *containerView = [transitionContext containerView];
    NSTimeInterval duration = [self transitionDuration:transitionContext];
    
    // Get a snapshot of the thing cell we're transitioning from
     ClubCell *cell = [fromViewController.clubTable dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:[fromViewController.clubTable indexPathForSelectedRow]];
  //  ClubCell *cell = (ClubCell*)[fromViewController.clubTable cellForItemAtIndexPath:[fromViewController.clubTable indexPathForSelectedRow] ];
    UIView *cellImageSnapshot = [cell.clubAvatar snapshotViewAfterScreenUpdates:NO];
    
    CGRect aframe =[containerView convertRect:cell.clubAvatar.frame fromView:cell.clubAvatar.superview];

    cellImageSnapshot.frame = [containerView convertRect:cell.clubAvatar.frame fromView:cell.clubAvatar.superview];
   // cell.clubAvatar.hidden = YES;
    
    // Setup the initial view states
    toViewController.view.frame = [transitionContext finalFrameForViewController:toViewController];
    toViewController.view.alpha = 0;
    //toViewController.headerView.clubAvatarImage.hidden = YES;
    
    [containerView addSubview:toViewController.view];
    [containerView addSubview:cellImageSnapshot];
    
    [UIView animateWithDuration:duration animations:^{
        // Fade in the second view controller's view
        toViewController.view.alpha = 1.0;
        
        // Move the cell snapshot so it's over the second view controller's image view
                //CGRect aRect = CGRectMake(11, 14, 70, 70);
//        CGRect frame = [containerView convertRect:toViewController.headerView.clubAvatarImage.frame fromView:toViewController.view];
         CGRect frame = [containerView convertRect:CGRectMake(11, 14, 70, 70) fromView:toViewController.view];
        cellImageSnapshot.frame = frame;
    } completion:^(BOOL finished) {
        // Clean up
       // toViewController.headerView.clubAvatarImage.hidden = NO;
       // cell.hidden = NO;
        [cellImageSnapshot removeFromSuperview];
        
        // Declare that we've finished
        [transitionContext completeTransition:!transitionContext.transitionWasCancelled];
    }];
}

- (NSTimeInterval)transitionDuration:(id<UIViewControllerContextTransitioning>)transitionContext {
    return 3;// 0.3;
}

@end
