//
//  AddFriendButton.m
//  Clubbook
//
//  Created by Andrew on 7/31/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "AddFriendButton.h"

@implementation AddFriendButton

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

-(id) initWithCoder:(NSCoder *)aDecoder
{
    if ((self = [super initWithCoder:aDecoder])) {
        self.clipsToBounds = YES;
        
        CALayer * layer = [self layer];
        [layer setMasksToBounds:YES];
        [layer setCornerRadius:6]; //when radius is 0, the border is a rectangle
        [layer setBorderWidth:1.0];
        [layer setBorderColor:[[UIColor colorWithRed:233/255.0 green:233/255.0 blue:233/255.0 alpha:1] CGColor]];
        
    }
    return self;
}

/*
- (void) setAddFriendState:(NSString*) text
{
    [self setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    normalColor = [UIColor colorWithRed:63/255.0 green:210/255.0 blue:18/255.0 alpha:1.0];
    self.backgroundColor = normalColor;
    highlightedColor = [UIColor colorWithRed:115/255.0 green:178/255.0 blue:119/255.0 alpha:1.0];
    
    [self setTitle:text forState:UIControlStateNormal];
}

- (void) setConfirmFriendState:(NSString*) text
{
    [self setTitle:text forState:UIControlStateNormal];
    [self setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    normalColor = [UIColor colorWithRed:0.000 green:0.6 blue:1.000 alpha:1];
    self.backgroundColor = normalColor;
    highlightedColor = [UIColor colorWithRed:0.000 green:0.7 blue:1.000 alpha:1];
}

- (void) setUnFriendState:(NSString*) text
{
    [self setTitle:text forState:UIControlStateNormal];
    [self setTitleColor:[UIColor colorWithRed:100/255.0 green:100/255.0 blue:100/255.0 alpha:1.0] forState:UIControlStateNormal];
    normalColor = [UIColor whiteColor];
    self.backgroundColor = normalColor;
    highlightedColor = [UIColor colorWithRed:138/255.0 green:138/255.0 blue:138/255.0 alpha:1.0];
}

- (void) setSentFriendRequestState:(NSString*) text
{
    [self setTitle:text forState:UIControlStateNormal];
    [self setTitleColor:[UIColor colorWithRed:100/255.0 green:100/255.0 blue:100/255.0 alpha:1.0] forState:UIControlStateNormal];

    normalColor = [UIColor whiteColor];
    self.backgroundColor = normalColor;
    highlightedColor = [UIColor whiteColor];
}*/

- (void) setButtonState:(NSString *) state;
{
    self.friendState = state;
    
    CGFloat fontSize = 14.0;

    if ([state isEqualToString:NSLocalizedString(@"noneFriend", nil)]) {
        [self setTitle:@"Add Friend" forState:UIControlStateNormal];
        [self setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        normalColor = [UIColor colorWithRed:63/255.0 green:210/255.0 blue:18/255.0 alpha:1.0];
        self.backgroundColor = normalColor;
        highlightedColor = [UIColor colorWithRed:115/255.0 green:178/255.0 blue:119/255.0 alpha:1.0];
     } else if ([state isEqualToString:NSLocalizedString(@"receiveRequest", nil)]){
         fontSize = 11.0;
         [self setTitle:@"Accept Friend Request" forState:UIControlStateNormal];
         [self setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
         normalColor = [UIColor colorWithRed:0.000 green:0.6 blue:1.000 alpha:1];
         self.backgroundColor = normalColor;
         highlightedColor = [UIColor colorWithRed:0.000 green:0.7 blue:1.000 alpha:1];
     } else if ([state isEqualToString:NSLocalizedString(@"sentRequest", nil)]){
         fontSize = 11.0;
         [self setTitle:@"Sent Friend Request" forState:UIControlStateNormal];
         [self setTitleColor:[UIColor colorWithRed:100/255.0 green:100/255.0 blue:100/255.0 alpha:1.0] forState:UIControlStateNormal];
         
         normalColor = [UIColor whiteColor];
         self.backgroundColor = normalColor;
         highlightedColor = [UIColor whiteColor];
     } else{
         [self setTitle:@"Unfriend" forState:UIControlStateNormal];
         [self setTitleColor:[UIColor colorWithRed:100/255.0 green:100/255.0 blue:100/255.0 alpha:1.0] forState:UIControlStateNormal];
         normalColor = [UIColor whiteColor];
         self.backgroundColor = normalColor;
         highlightedColor = [UIColor colorWithRed:138/255.0 green:138/255.0 blue:138/255.0 alpha:1.0];
    }
    
    self.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:fontSize];

}

- (UIColor*) backgroundColorForState:(UIControlState) _state {
    return [backgroundStates objectForKey:[NSNumber numberWithInt:_state]];
}

/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect
 {
 // Drawing code
 }
 */

- (void) setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        self.backgroundColor = highlightedColor;
    }
    else {
        self.backgroundColor = normalColor;
    }
}


@end
