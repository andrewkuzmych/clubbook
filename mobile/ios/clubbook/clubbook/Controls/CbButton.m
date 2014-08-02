//
//  CbButton.m
//  Clubbook
//
//  Created by Andrew on 7/4/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "CbButton.h"
#import <QuartzCore/QuartzCore.h>

@implementation CbButton

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


- (void) setMainState:(NSString*) text
{
    normalColor = [UIColor colorWithRed:63/255.0 green:210/255.0 blue:18/255.0 alpha:1.0];
    self.backgroundColor = normalColor;
    highlightedColor = [UIColor colorWithRed:115/255.0 green:178/255.0 blue:119/255.0 alpha:1.0];

    [self setTitle:text forState:UIControlStateNormal];
    self.isCheckin = NO;
}

- (void) setSecondState:(NSString*) text
{
    [self setTitle:text forState:UIControlStateNormal];
    normalColor = [UIColor colorWithRed:161/255.0 green:23/255.0 blue:24/255.0 alpha:1.0];
    self.backgroundColor = normalColor;
    highlightedColor = [UIColor colorWithRed:245/255.0 green:35/255.0 blue:36/255.0 alpha:1.0];
    self.isCheckin = YES;
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
